// Copyright 2017 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.skyframe.serialization.strings;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.skyframe.serialization.CodecRegisterer;
import com.google.devtools.build.lib.skyframe.serialization.DeserializationContext;
import com.google.devtools.build.lib.skyframe.serialization.ObjectCodec;
import com.google.devtools.build.lib.skyframe.serialization.SerializationContext;
import com.google.devtools.build.lib.unsafe.StringUnsafe;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import java.io.IOException;

/**
 * Dead-simple serialization for {@link String}s.
 *
 * <p>Only used when the more performant {@link UnsafeJdk9StringCodec} is not available.
 */
@VisibleForTesting
public final class StringCodec implements ObjectCodec<String> {

  /**
   * Returns the best available codec for Strings.
   *
   * <p>{@link UnsafeJdk9StringCodec} is more performant than {@link StringCodec}, but is only
   * available on JDK 9+.
   */
  @VisibleForTesting
  public static ObjectCodec<String> getBestAvailable() {
    return StringUnsafe.canUse()
        ? new UnsafeJdk9StringCodec(StringUnsafe.getInstance())
        : new StringCodec();
  }

  @Override
  public Class<String> getEncodedClass() {
    return String.class;
  }

  @Override
  public boolean autoRegister() {
    return false; // StringCodecRegisterer below registers the best available codec.
  }

  @Override
  public MemoizationStrategy getStrategy() {
    // Don't memoize strings inside memoizing serialization, to preserve current behavior.
    // TODO(janakr,brandjon,michajlo): Is it actually a problem to memoize strings? Doubt there
    // would be much performance impact from increasing the size of the identity map, and we
    // could potentially drop our string tables in the future.
    return MemoizationStrategy.DO_NOT_MEMOIZE;
  }

  @Override
  public void serialize(SerializationContext context, String str, CodedOutputStream codedOut)
      throws IOException {
    codedOut.writeStringNoTag(str);
  }

  @Override
  public String deserialize(DeserializationContext context, CodedInputStream codedIn)
      throws IOException {
    return codedIn.readString();
  }

  @SuppressWarnings("unused") // Used reflectively.
  private static final class StringCodecRegisterer implements CodecRegisterer {

    @Override
    public ImmutableList<ObjectCodec<?>> getCodecsToRegister() {
      return ImmutableList.of(getBestAvailable());
    }
  }
}
