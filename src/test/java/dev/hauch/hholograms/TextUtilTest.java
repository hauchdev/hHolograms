package dev.hauch.hholograms;

import dev.hauch.hholograms.util.TextUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TextUtilTest {
    @Test
    void stripsDangerousTagsForUntrusted() {
        String in = "<red>Hello</red> <click:run_command:/op>click</click> <hover:show_text:'x'>h</hover>";
        String out = TextUtil.sanitize(in, false);
        assertFalse(out.contains("click:"));
        assertFalse(out.contains("hover:"));
        assertTrue(out.contains("<red>Hello</red>"));
    }

    @Test
    void trustedKeepsInput() {
        String in = "<click:run_command:/say hi>click</click>";
        assertEquals(in, TextUtil.sanitize(in, true));
    }
}