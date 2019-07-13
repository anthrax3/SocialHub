package net.socialhub.define.service.slack;

import net.socialhub.define.AttributedTypes;
import net.socialhub.model.common.AttributedType;

import java.util.Arrays;
import java.util.List;

import static net.socialhub.define.service.slack.SlackAttributedTypes.Regex.*;

public class SlackAttributedTypes {

    public static AttributedType fullLink =
            new AttributedType.CommonAttributedType(SLACK_FULL_URL_REGEX, //
                    (m) -> (m.groupCount() > 0) ? m.group(1) : m.group());

    public static AttributedType email =
            new AttributedType.CommonAttributedType(SLACK_MAIL_REGEX, //
                    (m) -> (m.groupCount() > 0) ? m.group(1) : m.group());

    public static AttributedType mention =
            new AttributedType.CommonAttributedType(SLACK_MENTION_REGEX, //
                    (m) -> (m.groupCount() > 0) ? m.group(1) : m.group());

    public static List<AttributedType> slack() {
        return Arrays.asList( //
                SlackAttributedTypes.fullLink, //
                SlackAttributedTypes.email, //
                SlackAttributedTypes.mention);
    }

    public static class Regex {

        /** Slack での URL の正規表現 */
        public static final String SLACK_FULL_URL_REGEX = "<(" + AttributedTypes.Regex.FULL_URL_REGEX + ")>";

        /** Slack での Mention の正規表現 */
        public static final String SLACK_MENTION_REGEX = "<(@[A-Z0-9]+)>";

        /** Slack での Mail の正規表現 */
        public static final String SLACK_MAIL_REGEX = "<mailto:(" + AttributedTypes.Regex.SIMPLE_EMAIL_REGEX + ")>";
    }
}
