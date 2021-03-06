// Copyright 2021 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.packages.util;

import java.io.IOException;

/** Creates mock BUILD files required for the genrule rule. */
public final class MockGenruleSupport {
  /** Sets up mocking support for genrules. */
  public static void setup(MockToolsConfig config) throws IOException {
    config.create("embedded_tools/tools/genrule/BUILD", "exports_files(['genrule-setup.sh'])");
    config.create("embedded_tools/tools/genrule/genrule-setup.sh");
  }
}
