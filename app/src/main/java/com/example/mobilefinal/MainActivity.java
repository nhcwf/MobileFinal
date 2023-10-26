package com.example.mobilefinal;

import static com.example.mobilefinal.DatabaseHelper.DATABASE_NAME;
import static com.example.mobilefinal.DatabaseHelper.DATABASE_VERSION;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int DROP_BOXES_MAX_SIZE = 9;
    public static final String ADD_IMAGE_BUTTON_FIRST_CLICK = "firstClick";
    public static final String CURRENT_SCORE = "score";
    public static final String GAME_STARTED_TIME = "startTime";
    public int scoreCount;
    public int currentScore = 0;
    boolean image_default_position_is_not_saved = true;
    boolean is_add_image_button_first_click = true;
    long startTimeMillisecond, endTimeMillisecond;
    float imageX, imageY;
    DatabaseHelper database;
    ImageView importedImage, removeImage;
    ArrayList<ImageView> dropBoxes = new ArrayList<>(DROP_BOXES_MAX_SIZE);
    Button addImage, stop;
    ConstraintLayout mainLayout;
    Bitmap bitmap;
    TextView username;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DatabaseHelper(MainActivity.this, DATABASE_NAME, null, DATABASE_VERSION);
        scoreCount = database.getScoresCount();

        username = (TextView) findViewById(R.id.tv_username);
        username.setText(getUsernameString());

        addImage = (Button) findViewById(R.id.btn_add_image);
        addImage.setOnClickListener(addImageOnClickListener);

        stop = (Button) findViewById(R.id.btn_finish);
        stop.setVisibility(View.INVISIBLE);

        importedImage = (ImageView) findViewById(R.id.iv_imported_image);
        importedImage.setOnLongClickListener(importedImageOnLongClickListener);

        removeImage = (ImageView) findViewById(R.id.iv_remove_image);
        removeImage.setOnDragListener(removeImageOnDragListener);

        mainLayout = (ConstraintLayout) findViewById(R.id.cl_main_layout);
        mainLayout.setOnDragListener(mainLayoutOnDragListener);

        dropBoxes.add((ImageView) findViewById(R.id.iv_dropbox_1));
        dropBoxes.add((ImageView) findViewById(R.id.iv_dropbox_2));
        dropBoxes.add((ImageView) findViewById(R.id.iv_dropbox_3));
        dropBoxes.add((ImageView) findViewById(R.id.iv_dropbox_4));
        dropBoxes.add((ImageView) findViewById(R.id.iv_dropbox_5));
        dropBoxes.add((ImageView) findViewById(R.id.iv_dropbox_6));
        dropBoxes.add((ImageView) findViewById(R.id.iv_dropbox_7));
        dropBoxes.add((ImageView) findViewById(R.id.iv_dropbox_8));
        dropBoxes.add((ImageView) findViewById(R.id.iv_dropbox_9));
        for (ImageView dropbox : dropBoxes) { dropbox.setOnDragListener(dropboxOnDragListener); }
    }

    public void runTimer() {
        if (is_add_image_button_first_click) {
            startTimeMillisecond = Calendar.getInstance().getTimeInMillis();
            is_add_image_button_first_click = false;
            stop.setVisibility(View.VISIBLE);
            stop.setOnClickListener(stopOnClickListener);
            Toast.makeText(MainActivity.this, "Game started!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ADD_IMAGE_BUTTON_FIRST_CLICK, is_add_image_button_first_click);
        outState.putLong(GAME_STARTED_TIME, startTimeMillisecond);
        outState.putInt(CURRENT_SCORE, currentScore);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        is_add_image_button_first_click = savedInstanceState.getBoolean(ADD_IMAGE_BUTTON_FIRST_CLICK);
        startTimeMillisecond = savedInstanceState.getLong(GAME_STARTED_TIME);
        currentScore = savedInstanceState.getInt(CURRENT_SCORE);
    }

    private String getUsernameString() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(LoginActivity.USERNAME_BUNDLE);

        String username = "";
        if (bundle != null) { username = Objects.requireNonNull(bundle).getString(LoginActivity.USERNAME_STRING); }
        return username;
    }

    View.OnClickListener addImageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mimeType = "image/*";
            imagePicker.launch(new Intent().setType(mimeType).setAction(Intent.ACTION_GET_CONTENT));
        }
    };

    ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();

            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                importedImage.setImageBitmap(bitmap);
                importedImage.setVisibility(View.VISIBLE);

                if (image_default_position_is_not_saved) {
                    imageX = importedImage.getX();
                    imageY = importedImage.getY();
                    image_default_position_is_not_saved = false;
                }

                importedImage.setX(imageX);
                importedImage.setY(imageY);

                runTimer();
            }
        }
    });

    View.OnClickListener stopOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            endTimeMillisecond = Calendar.getInstance().getTimeInMillis();

            scoreCount++;
            database.insertGameSession(scoreCount, currentScore, endTimeMillisecond - startTimeMillisecond);

            startActivity(new Intent(MainActivity.this, ResultsActivity.class));
        }
    };

    View.OnLongClickListener importedImageOnLongClickListener = v -> {
        ClipData dragData = ClipData.newPlainText("", "");
        View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);

        v.startDragAndDrop(dragData, dragShadowBuilder,  v, 0);

        v.setVisibility(View.INVISIBLE);

        return true;
    };

    View.OnDragListener removeImageOnDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundResource(R.drawable.rounded_corner_light_green);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    break;
                case DragEvent.ACTION_DROP:
                    importedImage.setImageBitmap(null);
            }

            return true;
        }
    };

    View.OnDragListener mainLayoutOnDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                importedImage.setVisibility(View.VISIBLE);
                importedImage.setX(event.getX() - (float) importedImage.getHeight() / 2);
                importedImage.setY(event.getY() - (float) importedImage.getHeight() / 2);
            }
            return true;
        }
    };

    View.OnDragListener dropboxOnDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundResource(R.color.light_yellow);
                    currentScore++;
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundResource(R.color.light_green);
                    break;
                case DragEvent.ACTION_DROP:
                    ((ImageView) v).setImageBitmap(bitmap);

                    v.setOnLongClickListener( view -> {
                        ClipData dragData = ClipData.newPlainText("", "");
                        View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);

                        view.startDragAndDrop(dragData, dragShadowBuilder,  view, 0);

                        ((ImageView) v).setImageBitmap(null);

                        return true;
                    });

                    importedImage.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
    };
}