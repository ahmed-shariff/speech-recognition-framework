package system.core.RecognizerEngineComponents;

/*
 * Copyright 2013 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */


import java.io.IOException;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.frontend.util.StreamDataSource;


/**
 * High-level class for live speech recognition.
 */
public class LiveSpeechRecognizerExtention extends AbstractSpeechRecognizer {

    private final MicroPhoneExtention microphone;

    /**
     * Constructs new live recognition object.
     *
     * @param configuration common configuration
     */
    public LiveSpeechRecognizerExtention(Configuration configuration) throws IOException
    {
        super(new Context("resource:/"+"default.config.xml",configuration));
        microphone =new  MicroPhoneExtention(16000, 16, true, false);
        ((StreamDataSource) context.getInstance(StreamDataSource.class)).setInputStream(microphone.getStream());
    }

    public LiveSpeechRecognizerExtention(Configuration configuration,String defaultConfigPath) throws IOException
    {
        super(new Context(defaultConfigPath,configuration));
        microphone =new  MicroPhoneExtention(16000, 16, true, false);
        ((StreamDataSource) context.getInstance(StreamDataSource.class)).setInputStream(microphone.getStream());
    }
    /**
     * Starts recognition process.
     *
     * @param clear clear cached microphone data
     * @see         LiveSpeechRecognizerExtention#stopRecognition()
     */
    public void startRecognition(boolean clear) {
    		recognizer.allocate();
        microphone.startRecording();
    }

    /**
     * Stops recognition process.
     *
     * Recognition process is paused until the next call to startRecognition.
     *
     * @see LiveSpeechRecognizerExtention#startRecognition(boolean)
     */
    public void stopRecognition() {
        microphone.stopRecording();
        recognizer.deallocate();
    }
    
    public void closeRecognitionLine(){
    	microphone.closeLine();
    }
}
