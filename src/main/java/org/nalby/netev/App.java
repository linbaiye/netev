package org.nalby.netev;

import java.io.IOException;

public class App {
	
    /**
     * @param args
     * @throws IOException 
     */
    public static void main( String[] args ) throws IOException {
    	new Dispatcher("localhost", 9998).start();;
    }
}
