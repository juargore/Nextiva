/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xml.converters;

import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftSimultaneousRingLocation;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftSimultaneousRingPersonalLocations;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class NilValueSimultaneousRingLocationsConverter implements Converter<BroadsoftSimultaneousRingPersonalLocations> {

    @Override
    public BroadsoftSimultaneousRingPersonalLocations read(InputNode node) {
        return null;
    }

    @Override
    public void write(OutputNode node, BroadsoftSimultaneousRingPersonalLocations value) throws Exception {
        if (value == null ||
                value.getSimultaneousRingLocations() == null ||
                value.getSimultaneousRingLocations().isEmpty()) {

            node.setAttribute("xs:nil", "true");
            node.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema-instance");

        } else {
            if (value.getSimultaneousRingLocations() != null) {
                Serializer serializer = new Persister(new AnnotationStrategy());

                for (BroadsoftSimultaneousRingLocation broadsoftSimultaneousRingLocation : value.getSimultaneousRingLocations()) {
                    serializer.write(broadsoftSimultaneousRingLocation, node);
                }
            }
        }
    }
}