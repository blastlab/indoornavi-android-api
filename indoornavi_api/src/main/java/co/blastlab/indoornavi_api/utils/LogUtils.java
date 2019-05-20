package co.blastlab.indoornavi_api.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for writing logs to file.
 */
public class LogUtils {

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@SuppressLint("SimpleDateFormat")
	public static void logToFile(Context context, String fileName, String logText) {
		try {
			File testFile = new File(context.getExternalFilesDir(null), fileName);
			if (!testFile.exists())
				testFile.createNewFile();

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String dateString = dateFormat.format(new Date());

			BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true /*append*/));
			writer.write(dateString + ": " + logText);
			writer.newLine();
			writer.close();
		} catch (Exception e) {
			Log.e("ReadWriteFile", "Unable to write to the TestFile.txt file.");
		}
	}
}
