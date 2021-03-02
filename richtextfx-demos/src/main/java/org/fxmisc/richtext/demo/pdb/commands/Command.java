package org.fxmisc.richtext.demo.pdb.commands;

import org.fxmisc.richtext.demo.pdb.codec.Deserializer;

import java.util.concurrent.BlockingQueue;

public interface Command{

    String getCMDStr(String... args);
    <T extends Command> Deserializer<T> newDeserializer();
}
