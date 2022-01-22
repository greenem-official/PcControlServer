package pcControl;

import java.util.NoSuchElementException;
import java.util.Scanner;

import pcControl.logging.GeneralLogger;

public class InputRunnable implements Runnable {

	private static Scanner inputScanner;
	private static boolean stopped = false;
	
	@Override
	public void run() {
		//GeneralLogger.log("InputRunnable started!");
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
				//GeneralLogger.log("Input Thread: No Such Element Exception"); // too scary lol
				GeneralLogger.log("Closing input");
				stopped = true;
			}
			//GeneralLogger.log("You typed " + input);
			PcControlMain.getInstance().onConsoleCommand(input);
//			GeneralLogger.log("input");
		}
	}
	
	public static void onExit() {
		GeneralLogger.log("Closing input");
		inputScanner.close();
	}
}
