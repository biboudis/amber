/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.lang;

import java.lang.invoke.*;
import java.util.List;
import java.util.Objects;

/**
 * Simple concatenation policy.
 */
final class ConcatPolicy implements TemplatePolicy.Linkage<String, RuntimeException> {
    @Override
    public String apply(TemplatedString templatedString) {
        Objects.requireNonNull(templatedString);

        return templatedString.concat();
    }

    @Override
    public MethodHandle applier(MethodHandles.Lookup lookup,
                                MethodType type, String template) {
        Objects.requireNonNull(lookup);
        Objects.requireNonNull(type);
        Objects.requireNonNull(template);

        try {
            List<String> segments = TemplatedString.split(template);
            MethodType concatType = type.dropParameterTypes(0, 1);
            MethodHandle concatMH =
                    StringConcatFactory.makeConcatWithTemplate(segments,
                            concatType.parameterList());

            return MethodHandles.dropArguments(concatMH, 0,
                    type.parameterType(0));
        } catch (StringConcatException ex) {
            throw new AssertionError("StringConcatFactory failure", ex);
        }
    }

}
