package com.nextiva.nextivaapp.android.sip;

import static org.junit.Assert.assertEquals;

import com.nextiva.nextivaapp.android.mocks.values.SipMessagesMock;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 11/1/18.
 */
public class SipMessageTest {
    private SipMessage mSipMessageWithoutName = new SipMessage();
    private SipMessage mSipMessageWithName = new SipMessage();
    private SipMessage mSipMessageInviteWithVideoSendRecv = new SipMessage();
    private SipMessage mSipMessageInviteWithVideoNoPort = new SipMessage();
    private SipMessage mSipMessageInviteWithVideoInactiveWithPort = new SipMessage();
    private SipMessage mSipMessageInviteWithVideoInactive = new SipMessage();


    @Before
    public void init() {
        mSipMessageWithName = SipMessagesMock.WITH_NAME;
        mSipMessageWithoutName = SipMessagesMock.WITHOUT_NAME;
        mSipMessageInviteWithVideoSendRecv = SipMessagesMock.INVITE_WITH_VIDEO_SEND_RECV;
        mSipMessageInviteWithVideoNoPort = SipMessagesMock.INVITE_WITH_VIDEO_NO_PORT;
        mSipMessageInviteWithVideoInactiveWithPort = SipMessagesMock.INVITE_WITH_VIDEO_INACTIVE_WITH_PORT;
        mSipMessageInviteWithVideoInactive = SipMessagesMock.INVITE_WITH_VIDEO_INACTIVE;

    }

    @Test
    public void parseSipMessage_Parsed_assertedIdentity() {
        assertEquals("\"Thad Dannar\" <sip:4424440744@nextiva.com;user=phone", mSipMessageWithName.getAssertedIdentity());
    }

    @Test
    public void parseSipMessage_ParsedWithOutName_assertedIdentity() {
        assertEquals("<sip:4424440744@nextiva.com;user=phone", mSipMessageWithoutName.getAssertedIdentity());
    }

    @Test
    public void parseSipMessage_Parsed_assertedName() {
        assertEquals("Thad Dannar", mSipMessageWithName.getAssertedName());
    }

    @Test
    public void parseSipMessage_Parsed_assertedPhone() {
        assertEquals("4424440744", mSipMessageWithName.getAssertedPhone());
    }

    @Test
    public void parseSipMessage_Parsed_contact() {
        assertEquals("4424440744", mSipMessageWithName.getContact());
    }

    @Test
    public void parseSipMessage_Parsed_to() {
        assertEquals("4424440744", mSipMessageWithName.getTo());
    }

    @Test
    public void parseSipMessage_Parsed_from() {
        assertEquals("thad.dannar_5609_btbc_mb", mSipMessageWithName.getFrom());
    }

    @Test
    public void parseSipMessage_Parsed_video() {
        assertEquals("m=video 17538 RTP/AVP 109 102\n" +
                             "    b=AS:2048\n" +
                             "    a=sendrecv\n" +
                             "    a=rtpmap:109 H264/90000\n" +
                             "    a=fmtp:109 profile-level-id=42801E; packetization-mode=0\n" +
                             "    a=rtcp-fb:109 ccm fir\n" +
                             "    a=rtcp-fb:109 nack pli\n" +
                             "    a=rtcp-fb:109 nack\n" +
                             "    a=rtpmap:102 VP9/90000\n" +
                             "    a=fmtp:102 max-fr=25; max-fs=1620\n" +
                             "    a=rtcp-fb:102 ccm fir\n" +
                             "    a=rtcp-fb:102 nack pli\n" +
                             "    a=rtcp-fb:102 nack", mSipMessageInviteWithVideoSendRecv.getVideo());
    }

    @Test
    public void parseSipMessage_Parsed_videoWhenNoVideoExists() {
        assertEquals("", mSipMessageWithName.getVideo());
    }

    @Test
    public void parseSipMessage_ParsedWithOutName_assertedName() {
        assertEquals("", mSipMessageWithoutName.getAssertedName());
    }

    @Test
    public void parseSipMessage_ParsedWithOutName_assertedPhone() {
        assertEquals("4424440744", mSipMessageWithoutName.getAssertedPhone());
    }

    @Test
    public void parseSipMessage_ParsedWithOutName_contact() {
        assertEquals("4424440744", mSipMessageWithoutName.getContact());
    }

    @Test
    public void parseSipMessage_ParsedWithOutName_to() {
        assertEquals("4424440744", mSipMessageWithoutName.getTo());
    }

    @Test
    public void parseSipMessage_ParsedWithOutName_from() {
        assertEquals("thad.dannar_5609_btbc_mb", mSipMessageWithoutName.getFrom());
    }

}