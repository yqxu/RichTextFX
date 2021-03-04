package org.fxmisc.richtext.demo.pdb.codec;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yq
 */
public class InfoParser implements LineParser {

    @Override
    public Map<String, Object> parse(String str) {
        Map<String,Object> ret = new HashMap<>(2);
        ret.put("info",str);
        return ret;
    }

    @Override public boolean match(String str) {
        return true;
    }

    public static InfoParser newInstance() {
        InfoParser parser = new InfoParser();
        return parser;
    }
}
