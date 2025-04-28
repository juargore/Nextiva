/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xml.converters;

import android.text.TextUtils;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class NilValueStringConverter implements Converter<String> {
    @Override
    public String read(InputNode node) throws Exception {
        return node.getValue();
    }

    @Override
    public void write(OutputNode node, String value) {
        if (TextUtils.isEmpty(value)) {
            node.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema-instance");
            node.setAttribute("xs:nil", "true");

        } else {
            node.setValue(value);
        }
    }
}