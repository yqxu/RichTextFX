package org.fxmisc.richtext.demo.pdb.commands;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.codec.CodeLocationParser;
import org.fxmisc.richtext.demo.pdb.codec.CodeStrParser;
import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.codec.segment.Token;
import org.fxmisc.richtext.demo.pdb.codec.segment.TokenSegment;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

/**
 * @author yq
 */
public class CMDDetailInfoAsDict  extends CMDParam {

    @Override
    public String getCMDStr(String... args) {
        if (args == null || args.length <= 0 ){
            throw new RuntimeException("args can't be empty.");
        }
        String arg = args[0];
        return arg+".__dict__";
    }

}
