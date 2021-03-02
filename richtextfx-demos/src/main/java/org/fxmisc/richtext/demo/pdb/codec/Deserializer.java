package org.fxmisc.richtext.demo.pdb.codec;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.commands.Command;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public interface Deserializer<CMD extends Command> {
    CMDResp<CMD> parse(Map<String,Object> parts);
    Exception parseError(String str, BlockingQueue<String> errorQ);
    CompletableFuture<? extends CMDResp<CMD>> future();
    Queue<LineParser> parsers();
    
}
