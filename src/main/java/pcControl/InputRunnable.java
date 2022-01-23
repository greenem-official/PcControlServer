package pcControl;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;

import pcControl.data.References;
import pcControl.logging.GeneralLogger;

public class InputRunnable implements Runnable {
	private static Logger log = References.log4j;

	private static Scanner inputScanner;
	private static boolean stopped = false;
	
	@Override
	public void run() {
		//log.info("InputRunnable started!");
		String input = "";
		inputScanner = new Scanner(System.in);
		
		//jlineConsole.setCommands(finalCommands);
		
//		TerminalBuilder builder = TerminalBuilder.builder();
//        if (!OSUtils.IS_WINDOWS) {
//            builder.jna(false);
//        }
//		
//		History _history = new DefaultHistory();
//		Terminal _terminal = null;
//		try {
//			_terminal = builder.build();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		
//		LineReader _reader = LineReaderBuilder.builder()
//                .terminal(_terminal)
//                .history(_history)
//                .build();
//      _reader.unsetOpt(Option.INSERT_TAB);
		
		while (!stopped){
			try {
			input = inputScanner.nextLine();
			}
			catch(NoSuchElementException e) {
				//log.info("Input Thread: No Such Element Exception"); // too scary lol
				log.info("Closing input");
				stopped = true;
			}
			//log.info("You typed " + input);
			PcControlMain.getInstance().onConsoleCommand(input);
//			log.info("input");
		}
	}
	
	public static void onExit() {
		log.info("Closing input");
		inputScanner.close();
	}
}
