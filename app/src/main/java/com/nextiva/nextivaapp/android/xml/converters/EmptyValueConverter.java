/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xml.converters;

import android.text.TextUtils;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class EmptyValueConverter implements Converter<String> {
    @Override
    public String read(InputNode node) throws Exception {
        return node.getValue();
    }

    @Override
    public void write(OutputNode node, String value) {
        if (!TextUtils.isEmpty(value)) {
            node.setValue(value);
        }
    }
}