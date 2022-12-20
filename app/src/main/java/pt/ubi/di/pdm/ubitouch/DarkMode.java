package pt.ubi.di.pdm.ubitouch;

import android.content.Context;
import android.content.SharedPreferences;

public class DarkMode {
    SharedPreferences Darkmode;

    public DarkMode(Context context){
        Darkmode = context.getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
    }

    public void setDarkmodeState (Boolean state) {
        SharedPreferences.Editor editor = Darkmode.edit();
        editor.putBoolean("DarkModeEnabled", state);
        editor.apply();
    }

    public Boolean loadDarkMode () {
        Boolean state = Darkmode.getBoolean("DarkModeEnabled", false);
        return state;
    }
}

