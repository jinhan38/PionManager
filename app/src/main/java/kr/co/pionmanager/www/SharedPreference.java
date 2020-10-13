package kr.co.pionmanager.www;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedPreference {
    private static final String TAG = "SharedPreference";

    private static final String PREF_USER_ID = "userID";
    private static final String PREF_USER_NAME = "userName";
    private static final String PREF_USER_PASSWORD = "userPassword";
    private static final String PREF_CHECK = "checked";
    private static final String PREF_USER_NUM = "userNum";
    private static final String PREF_CLOSE = "foreverClose";
    private static final String PREF_USER_GRADE = "userGrade";
    private static final String PREF_USER_LICENSENUM = "userLicenseNum";
    private static final String PREF_USER_IDCARDIMAGE = "HasCardImage";
    private static final String PREF_USER_REFMEMBERNUM = "refMemberNum";
    private static final String PREF_AUTO_LOGIN = "autoLogin";
    private static final String PREF_USER_BANKNUM = "userBankNum";
    private static final String PREF_USER_Bank_Name = "userBankName";
    private static final String PREF_USER_BANK_Owner = "userBankOwner";
    private static final String PREF_USER_DOB = "userDOB";
    private static final String PREF_USER_REF_MANAGER_ID = "refManagerID";
    private static final String PREF_USER_PHONE = "userPhone";
    private static final String TOGGLE_PUSH_ALL = "toggle_push_all";
    private static final String TOGGLE_PUSH_AUCTION = "toggle_push_auction";
    private static final String TOGGLE_PUSH_NOTICE = "toggle_push_notice";
    private static final String TOGGLE_PUSH_MARKETING = "toggle_push_marketing";
    private static final String PREF_REF_MANAGER_GRADE = "ref_manager_grade";
    private static final String PREF_REF_MANAGER_NUM = "ref_manager_num";

    private static final String PREF_MANAGER_RATE = "manager_rate";
    private static final String PREF_TEAMLEADER_RATE = "teamleader_rate";
    private static final String PREF_PLATFORM_RATE = "platform_rate";
    private static final String PREF_REQUESTING_LICENSE = "requesting_license";



    static void setRefManagerNum(Context ctx, int ref_manager_num) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_REF_MANAGER_NUM, ref_manager_num);
        editor.apply();
    }

    public static int getRefManagerNum(Context ctx) {
        return getSharedPreferences(ctx).getInt(PREF_REF_MANAGER_NUM, 0);
    }


    static void setRequestingLicense(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_REQUESTING_LICENSE, "y");
        Log.d(TAG, "setRequestingLicense: " + "y");
        editor.apply();
    }

    static String getRequestingLicense(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_REQUESTING_LICENSE, "n");
    }

    static void setPlatformRate(Context ctx, String platform_rate) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PLATFORM_RATE, platform_rate);
        editor.apply();
    }

    static String getPlatformRate(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_PLATFORM_RATE, "");
    }

    static void setTeamLeaderRate(Context ctx, String teamleader_rate) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_TEAMLEADER_RATE, teamleader_rate);
        editor.apply();
    }

    static String getTeamLeaderRate(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_TEAMLEADER_RATE, "");
    }


    static void setManagerRate(Context ctx, String manager_rate) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_MANAGER_RATE, manager_rate);
        editor.apply();
    }

    static String getManagerRate(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_MANAGER_RATE, "");
    }

    static void setRefManagerGrade(Context ctx, String ref_manager_grade) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_REF_MANAGER_GRADE, ref_manager_grade);
        editor.apply();
    }

    public static String getRefManagerGrade(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_REF_MANAGER_GRADE, "");
    }


    public static void setTogglePushAll(Context ctx, String toggle_push_all) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(TOGGLE_PUSH_ALL, toggle_push_all);
        editor.apply();
    }

    public static String getTogglePushAll(Context ctx) {
        return getSharedPreferences(ctx).getString(TOGGLE_PUSH_ALL, "");
    }


    public static void setTogglePushAuction(Context ctx, String toggle_push_auction) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(TOGGLE_PUSH_AUCTION, toggle_push_auction);
        editor.apply();
    }

    static String getTogglePushAuction(Context ctx) {
        return getSharedPreferences(ctx).getString(TOGGLE_PUSH_AUCTION, "");
    }


    public static void setTogglePushNotice(Context ctx, String toggle_push_notice) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(TOGGLE_PUSH_NOTICE, toggle_push_notice);
        editor.apply();
    }

    static String getTogglePushNotice(Context ctx) {
        return getSharedPreferences(ctx).getString(TOGGLE_PUSH_NOTICE, "");
    }


    public static void setTogglePushMarketing(Context ctx, String toggle_push_marketing) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(TOGGLE_PUSH_MARKETING, toggle_push_marketing);
        editor.apply();
    }

    static String getTogglePushMarketing(Context ctx) {
        return getSharedPreferences(ctx).getString(TOGGLE_PUSH_MARKETING, "");
    }


    static void setUserPhone(Context ctx, String userPhone) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_PHONE, userPhone);
        editor.apply();
    }

    static String getUserPhone(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_PHONE, "");
    }

    static void setUserRefManagerID(Context ctx, String refManagerID) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_REF_MANAGER_ID, refManagerID);
        editor.apply();
    }

    static String getUserRefManagerID(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_REF_MANAGER_ID, "");
    }

    static void setUserDOB(Context ctx, String userDOB) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_DOB, userDOB);
        editor.apply();
    }

    static String getUserDOB(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_DOB, "");
    }

    static void setUserBankOwner(Context ctx, String userBankOwner) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_BANK_Owner, userBankOwner);
        editor.apply();
    }

    static String getUserBankOwner(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_BANK_Owner, "");
    }

    static void setUserBankName(Context ctx, String userBankName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_Bank_Name, userBankName);
        editor.apply();
    }

    static String getUserBankName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_Bank_Name, "");
    }

    static void setAutoLogin(Context ctx, String autoLogin) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_AUTO_LOGIN, autoLogin);
        editor.apply();
    }

    static String getAutoLogin(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_AUTO_LOGIN, "");
    }

    static void setRefMemberNum(Context ctx, int refMemberNum) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_USER_REFMEMBERNUM, refMemberNum);
        editor.apply();
    }

    static int getRefMemberNum(Context ctx) {
        return getSharedPreferences(ctx).getInt(PREF_USER_REFMEMBERNUM, 0);
    }


    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setCloseChecked(Context ctx, boolean checked) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_CLOSE, checked);
        editor.apply();
    }

    public static boolean getCloseChecked(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_CLOSE, false);
    }

    public static void setChecked(Context ctx, boolean checked) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_CHECK, checked);
        editor.apply();
    }

    static boolean getChecked(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_CHECK, false);
    }

    static void setUserNum(Context ctx, int userNum) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_USER_NUM, userNum);
        editor.apply();
    }

    static int getUserNum(Context ctx) {
        return getSharedPreferences(ctx).getInt(PREF_USER_NUM, 0);
    }

    static void setUserID(Context ctx, String userID) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_ID, userID);
        editor.apply();
    }

    static String getUserID(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    }

    static void setUserName(Context ctx, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.apply();
    }

    static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    static void setUserLicenseNum(Context ctx, String userLicenseNum) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_LICENSENUM, userLicenseNum);
        editor.apply();
    }

    static String getUserLicenseNum(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_LICENSENUM, "");
    }

    static void setUserBankNum(Context ctx, String userBankNum) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_BANKNUM, userBankNum);
        editor.apply();
    }

    static String getUserBankNum(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_BANKNUM, "");
    }

    static void setHasCardImage(Context ctx, String userIDCardImage) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_IDCARDIMAGE, userIDCardImage);
        editor.apply();
    }

    static String getHasCardImage(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_IDCARDIMAGE, "");
    }


    public static void setUserGrade(Context ctx, int userGrade) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_USER_GRADE, userGrade);
        editor.apply();
    }

    public static int getUserGrade(Context ctx) {
        return getSharedPreferences(ctx).getInt(PREF_USER_GRADE, 0);
    }

    public static void setUserPassword(Context ctx, String userPassword) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_PASSWORD, userPassword);
        editor.apply();
    }

    public static String getUserPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_PASSWORD, "");
    }

    public static void clearAll(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.apply();
    }

}