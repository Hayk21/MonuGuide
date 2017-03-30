package blue_team.com.monuguide.mapTouchable;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import blue_team.com.monuguide.fragments.MapStatueFragment;

public class TouchableWrapper extends FrameLayout{

    public TouchableWrapper(@NonNull Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                MapStatueFragment.mMapIsTouched = true;
                break;

            case MotionEvent.ACTION_UP:
                MapStatueFragment.mMapIsTouched = false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
