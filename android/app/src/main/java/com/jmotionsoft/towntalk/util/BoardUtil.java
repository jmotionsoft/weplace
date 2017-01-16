package com.jmotionsoft.towntalk.util;

import com.jmotionsoft.towntalk.R;

/**
 * Created by dooseon on 2016. 8. 23..
 */
public class BoardUtil {
    private static String ICON_PLACE = "PLACE";
    private static String ICON_HELP = "HELP";
    private static String ICON_MALL = "MALL";

    public static int getBoardIcon(String type){
        int icon = R.drawable.ic_board_default;

        if(type == null) return icon;

        if(ICON_PLACE.equals(type)){
            icon = R.drawable.ic_board_place;
        }else if(ICON_HELP.equals(type)){
            icon = R.drawable.ic_board_help;
        }else if(ICON_MALL.equals(type)){
            icon = R.drawable.ic_local_mall;
        }

        return icon;
    }
}
