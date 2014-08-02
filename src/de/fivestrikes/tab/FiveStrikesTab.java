package de.fivestrikes.tab;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FiveStrikesTab extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
/* Bildschirmgröße ermitteln */
		
		final int screenSize = getResources().getConfiguration().screenLayout &
		        Configuration.SCREENLAYOUT_SIZE_MASK;
		
/* Button einrichten */
		
		Button btnTextTeam = (Button) findViewById(R.id.main_text_team);
		Button btnTextGame = (Button) findViewById(R.id.main_text_game);
		
		ImageView imgMainImage=(ImageView) findViewById(R.id.main_image);
		imgMainImage.setImageResource(R.drawable.icon);
		scaleImage(imgMainImage, 96);
		
		ImageView imgMainImageText=(ImageView) findViewById(R.id.main_image_text);				
		imgMainImageText.setImageResource(R.drawable.fivestrikes);
		scaleImage(imgMainImageText, 432);
		
		ImageButton btnMainTeam=(ImageButton) findViewById(R.id.main_team);
		btnMainTeam.setImageResource(R.drawable.mannschaft);
		scaleImage(btnMainTeam, 72);
		
		ImageButton btnMainGame=(ImageButton) findViewById(R.id.main_game);
		btnMainGame.setImageResource(R.drawable.spiel);
		scaleImage(btnMainGame, 72);
		
		ImageButton btnMainPlayer=(ImageButton) findViewById(R.id.main_player);
		btnMainPlayer.setImageResource(R.drawable.spieler);
		scaleImage(btnMainPlayer, 72);

		ImageButton btnMainGameFast=(ImageButton) findViewById(R.id.main_game_fast);
		btnMainGameFast.setImageResource(R.drawable.schnellesspiel);
		scaleImage(btnMainGameFast, 72);
		ImageButton btnMainStatistic=(ImageButton) findViewById(R.id.main_statistic);
		btnMainStatistic.setImageResource(R.drawable.statistik);
		scaleImage(btnMainStatistic, 72);
		ImageButton btnMainImport=(ImageButton) findViewById(R.id.main_download);
		btnMainImport.setImageResource(R.drawable.download);
		scaleImage(btnMainImport, 72);
		
/* Button verlinken */

		btnMainTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	startActivity(new Intent(getApplicationContext(), TeamActivity.class));
            }
        });
		
		btnTextTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	startActivity(new Intent(getApplicationContext(), TeamActivity.class));
            }
        });
		
		btnMainGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	startActivity(new Intent(getApplicationContext(), GameActivity.class));
            }
        });
		
		btnTextGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	startActivity(new Intent(getApplicationContext(), GameActivity.class));
            }
        });
		
	}
	
	public void scaleImage(ImageView view, int boundBoxInDp)
    {
        // Get the ImageView and its bitmap
        Drawable drawing = view.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        // Get current dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) boundBoxInDp) / width;
        float yScale = ((float) boundBoxInDp) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
	
	
}
