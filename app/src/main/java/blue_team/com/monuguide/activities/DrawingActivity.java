        package blue_team.com.monuguide.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import blue_team.com.monuguide.R;
import blue_team.com.monuguide.custom_views.PaintView;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class DrawingActivity extends AppCompatActivity {

    public static final int SMALL_BRUSH = 10, MEDIUM_BRUSH = 30, BIG_BRUSH = 50;
    public static final int SMALL_ERASER = 30, MEDIUM_ERASER = 60, BIG_ERASER = 90;
    private Monument mMonument;
    private int mSize;
    private PaintView mPaintView;
    private LinearLayout mFirstListOfColors, mSecondListOfColors, mListOfTools;
    private ImageButton mCurrentCollor;
    private AlertDialog mAlertDialog;
    private String[] arrayOfItems = {"Anonymous","With your name"};
    private FireHelper mFireHelper = new FireHelper();
    private boolean isAnonymous = true;

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    View.OnClickListener OnColorItemClickListtener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String color = view.getTag().toString();
            mPaintView.setColor(color);
            mCurrentCollor.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            mCurrentCollor = (ImageButton) view;
            mCurrentCollor.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        }
    };


    View.OnClickListener OnToolsItemClickListenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.brush_button:
                    final Dialog brushDialog = new Dialog(DrawingActivity.this);

                    View.OnClickListener OnBrushSizeItemClickListenner = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.small_brush:
                                    mPaintView.setBrushSize(SMALL_BRUSH);
                                    brushDialog.dismiss();
                                    break;
                                case R.id.medium_brush:
                                    mPaintView.setBrushSize(MEDIUM_BRUSH);
                                    brushDialog.dismiss();
                                    break;
                                case R.id.big_brush:
                                    mPaintView.setBrushSize(BIG_BRUSH);
                                    brushDialog.dismiss();
                                    break;
                            }
                        }
                    };
                    brushDialog.setTitle("Choose you brush size");
                    brushDialog.setContentView(R.layout.brush_dialog);
                    ImageButton small_brush = (ImageButton) brushDialog.findViewById(R.id.small_brush);
                    ImageButton medium_brush = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
                    ImageButton big_brush = (ImageButton) brushDialog.findViewById(R.id.big_brush);
                    small_brush.setOnClickListener(OnBrushSizeItemClickListenner);
                    medium_brush.setOnClickListener(OnBrushSizeItemClickListenner);
                    big_brush.setOnClickListener(OnBrushSizeItemClickListenner);
                    brushDialog.show();
                    break;
                case R.id.eraser_button:
                    final Dialog brushDialog2 = new Dialog(DrawingActivity.this);
                    View.OnClickListener OnEraserSizeItemClickListenner = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.small_brush:
                                    mPaintView.setErased(SMALL_ERASER);
                                    brushDialog2.dismiss();
                                    break;
                                case R.id.medium_brush:
                                    mPaintView.setErased(MEDIUM_ERASER);
                                    brushDialog2.dismiss();
                                    break;
                                case R.id.big_brush:
                                    mPaintView.setErased(BIG_ERASER);
                                    brushDialog2.dismiss();
                                    break;
                            }
                        }
                    };
                    brushDialog2.setTitle("Choose your eraser size");
                    brushDialog2.setContentView(R.layout.brush_dialog);
                    ImageButton small_eraser = (ImageButton) brushDialog2.findViewById(R.id.small_brush);
                    ImageButton medium_eraser = (ImageButton) brushDialog2.findViewById(R.id.medium_brush);
                    ImageButton big_eraser = (ImageButton) brushDialog2.findViewById(R.id.big_brush);
                    small_eraser.setOnClickListener(OnEraserSizeItemClickListenner);
                    medium_eraser.setOnClickListener(OnEraserSizeItemClickListenner);
                    big_eraser.setOnClickListener(OnEraserSizeItemClickListenner);
                    brushDialog2.show();
                    break;
                case R.id.create_button:
                    AlertDialog.Builder builder = new AlertDialog.Builder(DrawingActivity.this);
                    builder.setMessage(getString(R.string.open_new_page)).setCancelable(true).setNegativeButton("Open", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mPaintView.newPage();
                        }
                    }).setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    mAlertDialog = builder.create();
                    mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                    mAlertDialog.show();
                    break;
                case R.id.save_button:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DrawingActivity.this);
                    mPaintView.setDrawingCacheEnabled(true);
                    builder2.setTitle(getString(R.string.save_drawing));
                    builder2.setSingleChoiceItems(arrayOfItems, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i == 0){
                                isAnonymous = true;
                            }else if(i == 1){
                                isAnonymous = false;
                            }
                        }
                    });
                    builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mPaintView.buildDrawingCache();
                            Bitmap bitmap = mPaintView.getDrawingCache();
                            Bitmap bitmap1 = bitmap.copy(Bitmap.Config.ARGB_8888,false);
                            bitmap.recycle();
                            bitmap = null;
                            if(mMonument != null) {
                                String myuser = mFireHelper.getCurrentUid();
                                if(myuser != null) {
                                    if (isAnonymous) {
                                        mFireHelper.addNote(bitmap1, mMonument, myuser, "Anonymous", mSize);
                                    } else {
                                        mFireHelper.addNote(bitmap1, mMonument, myuser, mFireHelper.getCurrentUserName(), mSize);
                                    }
                                }
                                PagerActivity.setFirstCommit(true);
                            }
                            mPaintView.destroyDrawingCache();
                            mPaintView.setDrawingCacheEnabled(false);
                            DrawingActivity.this.finish();
                        }
                    });
                    builder2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mAlertDialog.cancel();
                            mPaintView.setDrawingCacheEnabled(false);
                        }
                    });
                    mAlertDialog = builder2.create();
                    mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                    mAlertDialog.show();
                    break;
                case R.id.back_button:
                    DrawingActivity.this.finish();
                    overridePendingTransition(R.anim.draw_alpha_up, R.anim.draw_close_anim);
                    break;


            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawingActivity.this.finish();
        overridePendingTransition(R.anim.draw_alpha_up, R.anim.draw_close_anim);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        setupActionBar();

        Intent activityIntent = this.getIntent();
        if (activityIntent.getParcelableExtra(PagerActivity.EXTRA_WITH_MONUMENT) != null) {
            mMonument = activityIntent.getParcelableExtra(PagerActivity.EXTRA_WITH_MONUMENT);
            mSize = activityIntent.getIntExtra(PagerActivity.EXTRA_WITH_SIZE, 0);
        }
        mPaintView = (PaintView) findViewById(R.id.paintView);
        mFirstListOfColors = (LinearLayout) findViewById(R.id.list_of_colors_1);
        mSecondListOfColors = (LinearLayout) findViewById(R.id.list_of_colors_2);
        mListOfTools = (LinearLayout) findViewById(R.id.list_of_res);
        mCurrentCollor = (ImageButton) mFirstListOfColors.getChildAt(0);
        mCurrentCollor.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        setItemsOnClickListenners();


    }

    private void setItemsOnClickListenners() {
        for (int i = 0; i < 6; i++) {
            mFirstListOfColors.getChildAt(i).setOnClickListener(OnColorItemClickListtener);
        }
        for (int i = 0; i < 6; i++) {
            mSecondListOfColors.getChildAt(i).setOnClickListener(OnColorItemClickListtener);
        }
        for (int i = 0; i < 5; i++) {
            mListOfTools.getChildAt(i).setOnClickListener(OnToolsItemClickListenner);
        }
    }

}
