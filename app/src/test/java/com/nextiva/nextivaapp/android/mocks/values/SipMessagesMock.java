package com.nextiva.nextivaapp.android.mocks.values;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.StringDef;

import com.nextiva.nextivaapp.android.sip.SipMessage;

import java.lang.annotation.Retention;

/**
 * Created by Thaddeus Dannar on 2019-09-20.
 */
public class SipMessagesMock {

    public static final String WITHOUT_NAME_STRING = "SIP/2.0 183 Session Progress\n" +
            "Via: SIP/2.0/TCP 192.168.86.21:5062;received=68.109.180.117;branch=z9hG4bK-524287-1---0dc28055e049dc5a;rport=43065\n" +
            "Require: 100rel\n" +
            "Contact: <sip:4424440744@208.73.150.171:5062;transport=tcp>\n" +
            "To: <sip:4424440744@prod.voipdnsservers.com>;tag=1517975148-1541100011796\n" +
            "From: \"thad.dannar_5609_btbc_mb\"<sip:thad.dannar_5609_btbc_mb@prod.voipdnsservers.com>;tag=57eab346\n" +
            "Call-ID: WCtsfzo7TrUTDgmhGZSPZQ..\n" +
            "CSeq: 1 INVITE\n" +
            "Allow: ACK, BYE, CANCEL, INFO, INVITE, OPTIONS, PRACK, REFER, NOTIFY, UPDATE\n" +
            "Call-Info: <sip:10.104.100.101>;appearance-index=1\n" +
            "Content-Disposition: session;handling=required\n" +
            "Content-Type: application/sdp\n" +
            "Supported: \n" +
            "RSeq: 677118090\n" +
            "Privacy: none\n" +
            "P-Asserted-Identity: <sip:4424440744@nextiva.com;user=phone>\n" +
            "Session: Media\n" +
            "X-BroadWorks-Correlation-Info: de965684-a6b6-4e9c-97e0-15f419467fed\n" +
            "Content-Length: 219\n" +
            "\n" +
            "v=0\n" +
            "o=BroadWorks 61256359 1 IN IP4 208.73.150.171\n" +
            "s=-\n" +
            "c=IN IP4 208.73.150.171\n" +
            "t=0 0\n" +
            "m=audio 32380 RTP/AVP 0 101\n" +
            "a=rtpmap:0 PCMU/8000\n" +
            "a=rtpmap:101 telephone-event/8000\n" +
            "a=fmtp:101 0-15\n" +
            "a=sendrecv\n" +
            "a=maxptime:20\n";

    public static final String WITH_NAME_STRING = "SIP/2.0 183 Session Progress\n" +
            "Via: SIP/2.0/TCP 192.168.86.21:5062;received=68.109.180.117;branch=z9hG4bK-524287-1---0dc28055e049dc5a;rport=43065\n" +
            "Require: 100rel\n" +
            "Contact: <sip:4424440744@208.73.150.171:5062;transport=tcp>\n" +
            "To: <sip:4424440744@prod.voipdnsservers.com>;tag=1517975148-1541100011796\n" +
            "From: \"thad.dannar_5609_btbc_mb\"<sip:thad.dannar_5609_btbc_mb@prod.voipdnsservers.com>;tag=57eab346\n" +
            "Call-ID: WCtsfzo7TrUTDgmhGZSPZQ..\n" +
            "CSeq: 1 INVITE\n" +
            "Allow: ACK, BYE, CANCEL, INFO, INVITE, OPTIONS, PRACK, REFER, NOTIFY, UPDATE\n" +
            "Call-Info: <sip:10.104.100.101>;appearance-index=1\n" +
            "Content-Disposition: session;handling=required\n" +
            "Content-Type: application/sdp\n" +
            "Supported: \n" +
            "RSeq: 677118090\n" +
            "Privacy: none\n" +
            "P-Asserted-Identity: \"Thad Dannar\" <sip:4424440744@nextiva.com;user=phone>\n" +
            "Session: Media\n" +
            "X-BroadWorks-Correlation-Info: de965684-a6b6-4e9c-97e0-15f419467fed\n" +
            "Content-Length: 219\n" +
            "\n" +
            "v=0\n" +
            "o=BroadWorks 61256359 1 IN IP4 208.73.150.171\n" +
            "s=-\n" +
            "c=IN IP4 208.73.150.171\n" +
            "t=0 0\n" +
            "m=audio 32380 RTP/AVP 0 101\n" +
            "a=rtpmap:0 PCMU/8000\n" +
            "a=rtpmap:101 telephone-event/8000\n" +
            "a=fmtp:101 0-15\n" +
            "a=sendrecv\n" +
            "a=maxptime:20\n";


    public static final String INVITE_WITH_VIDEO_STRING = "SIP/2.0\n" +
            "    Via: SIP/2.0/TCP 208.89.110.80:5062;branch=z9hG4bKb2jesf108oh6vstkf8k0.1\n" +
            "    Max-Forwards: 19\n" +
            "    Contact: <sip:4154328526@208.89.110.80:5062;transport=tcp>\n" +
            "    To: \"mobtwo qa\"<sip:mobtwo_5552_btbc_mb@68.109.180.117>\n" +
            "    From: \"SAN FRANCSCO CA\"<sip:4154328526@208.89.110.80;user=phone>;tag=379157004-1568942218096-\n" +
            "    Call-ID: BW18165809619091913651821@10.106.100.101\n" +
            "    CSeq: 639577529 INVITE\n" +
            "    Accept: application/media_control+xml, application/sdp, multipart/mixed\n" +
            "    Allow: ACK, BYE, CANCEL, INFO, INVITE, OPTIONS, PRACK, REFER, NOTIFY\n" +
            "    Call-Info: <sip:10.106.100.101>;appearance-index=1;push-notification=\"45820292255#0\"\n" +
            "    Content-Type: application/sdp\n" +
            "    Supported: 100rel\n" +
            "    Recv-Info: x-broadworks-client-session-info\n" +
            "    X-BroadWorks-Correlation-Info: a6cbd197-ebf5-4a73-a5f2-00fa2d5c777b\n" +
            "    Content-Length: 643\n" +
            "    \n" +
            "    v=0\n" +
            "    o=BroadWorks 4952267021 1 IN IP4 208.89.110.80\n" +
            "    s=-\n" +
            "    c=IN IP4 208.89.110.80\n" +
            "    t=0 0\n" +
            "    m=audio 21528 RTP/AVP 9 0 8 18 120 101\n" +
            "    a=sendrecv\n" +
            "    a=rtpmap:9 G722/8000\n" +
            "    a=rtpmap:0 PCMU/8000\n" +
            "    a=rtpmap:8 PCMA/8000\n" +
            "    a=rtpmap:18 G729/8000\n" +
            "    a=fmtp:18 annexb=no\n" +
            "    a=rtpmap:120 opus/48000/2\n" +
            "    a=rtpmap:101 telephone-event/8000\n" +
            "    m=video 17538 RTP/AVP 109 102\n" +
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
            "    a=rtcp-fb:102 nack";

    public static final String INVITE_WITH_VIDEO_NO_PORT_STRING = "SIP/2.0\n" +
            "    Via: SIP/2.0/TCP 208.89.110.80:5062;branch=z9hG4bKb2jesf108oh6vstkf8k0.1\n" +
            "    Max-Forwards: 19\n" +
            "    Contact: <sip:4154328526@208.89.110.80:5062;transport=tcp>\n" +
            "    To: \"mobtwo qa\"<sip:mobtwo_5552_btbc_mb@68.109.180.117>\n" +
            "    From: \"SAN FRANCSCO CA\"<sip:4154328526@208.89.110.80;user=phone>;tag=379157004-1568942218096-\n" +
            "    Call-ID: BW18165809619091913651821@10.106.100.101\n" +
            "    CSeq: 639577529 INVITE\n" +
            "    Accept: application/media_control+xml, application/sdp, multipart/mixed\n" +
            "    Allow: ACK, BYE, CANCEL, INFO, INVITE, OPTIONS, PRACK, REFER, NOTIFY\n" +
            "    Call-Info: <sip:10.106.100.101>;appearance-index=1;push-notification=\"45820292255#0\"\n" +
            "    Content-Type: application/sdp\n" +
            "    Supported: 100rel\n" +
            "    Recv-Info: x-broadworks-client-session-info\n" +
            "    X-BroadWorks-Correlation-Info: a6cbd197-ebf5-4a73-a5f2-00fa2d5c777b\n" +
            "    Content-Length: 643\n" +
            "    \n" +
            "    v=0\n" +
            "    o=BroadWorks 4952267021 1 IN IP4 208.89.110.80\n" +
            "    s=-\n" +
            "    c=IN IP4 208.89.110.80\n" +
            "    t=0 0\n" +
            "    m=audio 21528 RTP/AVP 9 0 8 18 120 101\n" +
            "    a=sendrecv\n" +
            "    a=rtpmap:9 G722/8000\n" +
            "    a=rtpmap:0 PCMU/8000\n" +
            "    a=rtpmap:8 PCMA/8000\n" +
            "    a=rtpmap:18 G729/8000\n" +
            "    a=fmtp:18 annexb=no\n" +
            "    a=rtpmap:120 opus/48000/2\n" +
            "    a=rtpmap:101 telephone-event/8000\n" +
            "    m=video 17538 RTP/AVP 109 102\n" +
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
            "    a=rtcp-fb:102 nack";

    public static final String INVITE_WITH_VIDEO_INACTIVE_WITH_PORT_STRING = "SIP/2.0\n" +
            "    Via: SIP/2.0/TCP 208.89.110.80:5062;branch=z9hG4bKb2jesf108oh6vstkf8k0.1\n" +
            "    Max-Forwards: 19\n" +
            "    Contact: <sip:4154328526@208.89.110.80:5062;transport=tcp>\n" +
            "    To: \"mobtwo qa\"<sip:mobtwo_5552_btbc_mb@68.109.180.117>\n" +
            "    From: \"SAN FRANCSCO CA\"<sip:4154328526@208.89.110.80;user=phone>;tag=379157004-1568942218096-\n" +
            "    Call-ID: BW18165809619091913651821@10.106.100.101\n" +
            "    CSeq: 639577529 INVITE\n" +
            "    Accept: application/media_control+xml, application/sdp, multipart/mixed\n" +
            "    Allow: ACK, BYE, CANCEL, INFO, INVITE, OPTIONS, PRACK, REFER, NOTIFY\n" +
            "    Call-Info: <sip:10.106.100.101>;appearance-index=1;push-notification=\"45820292255#0\"\n" +
            "    Content-Type: application/sdp\n" +
            "    Supported: 100rel\n" +
            "    Recv-Info: x-broadworks-client-session-info\n" +
            "    X-BroadWorks-Correlation-Info: a6cbd197-ebf5-4a73-a5f2-00fa2d5c777b\n" +
            "    Content-Length: 643\n" +
            "    \n" +
            "    v=0\n" +
            "    o=BroadWorks 4952267021 1 IN IP4 208.89.110.80\n" +
            "    s=-\n" +
            "    c=IN IP4 208.89.110.80\n" +
            "    t=0 0\n" +
            "    m=audio 21528 RTP/AVP 9 0 8 18 120 101\n" +
            "    a=sendrecv\n" +
            "    a=rtpmap:9 G722/8000\n" +
            "    a=rtpmap:0 PCMU/8000\n" +
            "    a=rtpmap:8 PCMA/8000\n" +
            "    a=rtpmap:18 G729/8000\n" +
            "    a=fmtp:18 annexb=no\n" +
            "    a=rtpmap:120 opus/48000/2\n" +
            "    a=rtpmap:101 telephone-event/8000\n" +
            "    m=video 17538 RTP/AVP 109 102\n" +
            "    b=AS:2048\n" +
            "    a=inactive\n" +
            "    a=rtpmap:109 H264/90000\n" +
            "    a=fmtp:109 profile-level-id=42801E; packetization-mode=0\n" +
            "    a=rtcp-fb:109 ccm fir\n" +
            "    a=rtcp-fb:109 nack pli\n" +
            "    a=rtcp-fb:109 nack\n" +
            "    a=rtpmap:102 VP9/90000\n" +
            "    a=fmtp:102 max-fr=25; max-fs=1620\n" +
            "    a=rtcp-fb:102 ccm fir\n" +
            "    a=rtcp-fb:102 nack pli\n" +
            "    a=rtcp-fb:102 nack";

    public static final String INVITE_WITH_VIDEO_INACTIVE_STRING = "SIP/2.0\n" +
            "    Via: SIP/2.0/TCP 208.89.110.80:5062;branch=z9hG4bKb2jesf108oh6vstkf8k0.1\n" +
            "    Max-Forwards: 19\n" +
            "    Contact: <sip:4154328526@208.89.110.80:5062;transport=tcp>\n" +
            "    To: \"mobtwo qa\"<sip:mobtwo_5552_btbc_mb@68.109.180.117>\n" +
            "    From: \"SAN FRANCSCO CA\"<sip:4154328526@208.89.110.80;user=phone>;tag=379157004-1568942218096-\n" +
            "    Call-ID: BW18165809619091913651821@10.106.100.101\n" +
            "    CSeq: 639577529 INVITE\n" +
            "    Accept: application/media_control+xml, application/sdp, multipart/mixed\n" +
            "    Allow: ACK, BYE, CANCEL, INFO, INVITE, OPTIONS, PRACK, REFER, NOTIFY\n" +
            "    Call-Info: <sip:10.106.100.101>;appearance-index=1;push-notification=\"45820292255#0\"\n" +
            "    Content-Type: application/sdp\n" +
            "    Supported: 100rel\n" +
            "    Recv-Info: x-broadworks-client-session-info\n" +
            "    X-BroadWorks-Correlation-Info: a6cbd197-ebf5-4a73-a5f2-00fa2d5c777b\n" +
            "    Content-Length: 643\n" +
            "    \n" +
            "    v=0\n" +
            "    o=BroadWorks 4952267021 1 IN IP4 208.89.110.80\n" +
            "    s=-\n" +
            "    c=IN IP4 208.89.110.80\n" +
            "    t=0 0\n" +
            "    m=audio 21528 RTP/AVP 9 0 8 18 120 101\n" +
            "    a=sendrecv\n" +
            "    a=rtpmap:9 G722/8000\n" +
            "    a=rtpmap:0 PCMU/8000\n" +
            "    a=rtpmap:8 PCMA/8000\n" +
            "    a=rtpmap:18 G729/8000\n" +
            "    a=fmtp:18 annexb=no\n" +
            "    a=rtpmap:120 opus/48000/2\n" +
            "    a=rtpmap:101 telephone-event/8000\n" +
            "    m=video 0 RTP/AVP 109 102\n" +
            "    b=AS:2048\n" +
            "    a=inactive\n" +
            "    a=rtpmap:109 H264/90000\n" +
            "    a=fmtp:109 profile-level-id=42801E; packetization-mode=0\n" +
            "    a=rtcp-fb:109 ccm fir\n" +
            "    a=rtcp-fb:109 nack pli\n" +
            "    a=rtcp-fb:109 nack\n" +
            "    a=rtpmap:102 VP9/90000\n" +
            "    a=fmtp:102 max-fr=25; max-fs=1620\n" +
            "    a=rtcp-fb:102 ccm fir\n" +
            "    a=rtcp-fb:102 nack pli\n" +
            "    a=rtcp-fb:102 nack";

    @Retention(SOURCE)
    @StringDef( {
            WITH_NAME_STRING,
            WITHOUT_NAME_STRING,
            INVITE_WITH_VIDEO_STRING,
            INVITE_WITH_VIDEO_NO_PORT_STRING,
            INVITE_WITH_VIDEO_INACTIVE_WITH_PORT_STRING,
            INVITE_WITH_VIDEO_INACTIVE_STRING
    })
    public @interface SipMessageStrings {
    }

    public static final SipMessage WITH_NAME = new SipMessage().parseSipMessage(WITH_NAME_STRING);
    public static final SipMessage WITHOUT_NAME = new SipMessage().parseSipMessage(WITHOUT_NAME_STRING);
    public static final SipMessage INVITE_WITH_VIDEO_SEND_RECV = new SipMessage().parseSipMessage(INVITE_WITH_VIDEO_STRING);
    public static final SipMessage INVITE_WITH_VIDEO_NO_PORT = new SipMessage().parseSipMessage(INVITE_WITH_VIDEO_NO_PORT_STRING);
    public static final SipMessage INVITE_WITH_VIDEO_INACTIVE_WITH_PORT = new SipMessage().parseSipMessage(INVITE_WITH_VIDEO_INACTIVE_WITH_PORT_STRING);
    public static final SipMessage INVITE_WITH_VIDEO_INACTIVE = new SipMessage().parseSipMessage(INVITE_WITH_VIDEO_INACTIVE_STRING);

}
