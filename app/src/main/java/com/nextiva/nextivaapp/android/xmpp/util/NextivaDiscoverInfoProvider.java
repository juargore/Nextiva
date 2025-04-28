/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.util;

import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.xmlpull.v1.XmlPullParser;

/**
 * The DiscoverInfoProvider parses Service Discovery information packets.
 *
 * @author Gaston Dombiak
 */
public class NextivaDiscoverInfoProvider extends IQProvider<DiscoverInfo> {

    @Override
    public DiscoverInfo parse(XmlPullParser parser, int initialDepth)
            throws Exception {
        DiscoverInfo discoverInfo = new DiscoverInfo();
        boolean done = false;
        DiscoverInfo.Identity identity;
        String category = "";
        String identityName = "";
        String type = "";
        String variable = "";
        String lang = "";
        discoverInfo.setNode(parser.getAttributeValue("", "node"));
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                final String name = parser.getName();
                final String namespace = parser.getNamespace();
                if (namespace.equals(DiscoverInfo.NAMESPACE)) {
                    switch (name) {
                        case "identity":
                            // Initialize the variables from the parsed XML
                            category = parser.getAttributeValue("", "category");
                            identityName = parser.getAttributeValue("", "name");
                            type = parser.getAttributeValue("", "type");
                            lang = parser.getAttributeValue(parser.getNamespace("xml"), "lang");
                            break;
                        case "feature":
                            // Initialize the variables from the parsed XML
                            variable = parser.getAttributeValue("", "var");
                            break;
                    }
                }
                // Otherwise, it must be a packet extension.
                else {
                    PacketParserUtils.addExtensionElement(discoverInfo, parser);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("identity")) {
                    // Create a new identity and add it to the discovered info.
                    identity = new DiscoverInfo.Identity(category, type, identityName, lang);
                    discoverInfo.addIdentity(identity);
                }
                if (parser.getName().equals("feature")) {
                    // Create a new feature and add it to the discovered info.
                    boolean notADuplicateFeature = discoverInfo.addFeature(variable);

                    // line disabled to prevent crash
                    // assert (notADuplicateFeature);
                }
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }

        return discoverInfo;
    }
}
