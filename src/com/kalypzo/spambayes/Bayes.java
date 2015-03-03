package com.kalypzo.spambayes;

import java.io.IOException;

import sms.kalypzo.cleansms.R;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;

public class Bayes extends Activity {
	public static Context app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app=this;
		setContentView(R.layout.bayes);
		TextView cname= (TextView) findViewById(R.id.text);
		int test=Bayesian();
		if(test==1)
			cname.setText("I do believe this message is spam!");
		else if(test==0)
			cname.setText("I do believe this is a genuine message!");
		else if(test==2)
			cname.setText("Error!");
		}

	public static int Bayesian() {
		try {
			
			// Create a new SpamFilter Object
			SpamFilter filter = new SpamFilter(app);

			// Train spam with a file of spam e-mails
			//Toast t=Toast.makeText(c.getApplicationContext(),"jahsfjahs",Toast.LENGTH_LONG);
			//t.show();
			filter.trainSpam();
			// Train spam with a file of regular e-mails
			filter.trainGood();
			// We are finished adding words so finalize the results
			filter.finalizeTraining();

			// for (int i = 1; i < 4; i++) {
			// Read in a text file
			// A2ZFileReader fr = new A2ZFileReader("messages/mail" + i +
			// ".txt");
			String stuff = "text";

			// Ask the filter to analyze it
			boolean spam = filter.analyze(stuff);

			// Print results
			if (spam)
				return 1;//cname.setText("I do believe this message is spam!");
			else
				return 0;//cname.setText("I do believe this is a genuine message!");
			// }

		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		}
	}
}
