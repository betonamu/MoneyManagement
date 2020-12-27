package com.example.doanjava.common;

import android.content.Context;

public class GlobalFunction {
    protected static Context _context;

    public GlobalFunction(Context context){
        _context = context;
    }

    public String GetStringResource(int key){
        return _context.getString(key);
    }
}
