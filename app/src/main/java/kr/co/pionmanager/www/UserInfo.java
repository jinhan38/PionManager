package kr.co.pionmanager.www;

import android.app.Activity;
import android.util.Log;

public class UserInfo extends Activity {

    public UserInfo() {

    }

    private static int userNum = 0;
    private static int userGrade = 0;
    private static String userID = "";
    private static String userPassword = "";
    private static String userName = "";
    private static String userPhone = "";
    private static String userDOB = "";
    private static boolean isLogin = false;
    private static int isUpload = 0;
    private static String userLicenseNum = "";
    private static String userBankName = "";
    private static String userBankNum = "";
    private static String userBankOwner = "";
    private static String hasCardImage = "";
    private static int refMemberNum = 0;
    private static String teamMemberName_as_leader = "";
    private static String teamMemberNum_as_leader = "";
    private static String hopePrice = "";
    private static String addr = "";
    private static String LatestDealPrice = "";
    private static String loanNum = "";
    private static String auctionNumShort = "";
    private static String autoLogin = "";
    private static String memberName = "";
    private static String refManagerID = "";
    private static String pushAll = "";
    private static String pushAuction = "";
    private static String pushNotice = "";
    private static String pushMarketing = "";
    private static String refManagerGrade = "";
    private static String refManagerCount = "";
    private static String pwNotSecret = "";
    private static int refManagerNum = 0;
    private static int notice_1_num = 0;
    private static int notice_2_num = 0;
    private static int notice_3_num = 0;
    private static String needUpdate = "";


    private static String managerRate = "";//매니저 수당 비율
    private static String teamLeaderRate = ""; //팀장 수당 비율
    private static String platformRate = ""; //플랫폼 수수료

    private static boolean pushOpen = false;
    private static Object pushType = "";
    private static Object pushUserNum = "";
    private static String pushLoanNum = "";
    private static Object pushUrl = "";

    public static String getPushLoanNum() {
        return pushLoanNum;
    }

    public static void setPushLoanNum(String pushLoanNum) {
        UserInfo.pushLoanNum = pushLoanNum;
    }

    public static Object getPushUrl() {
        return pushUrl;
    }

    public static void setPushUrl(Object pushUrl) {
        UserInfo.pushUrl = pushUrl;
    }

    public static boolean getPushOpen() {
        return pushOpen;
    }

    public static void setPushOpen(boolean pushOpen) {
        UserInfo.pushOpen = pushOpen;
    }

    public static Object getPushType() {
        return pushType;
    }

    public static void setPushType(Object pushType) {
        UserInfo.pushType = pushType;
    }

    public static Object getPushUserNum() {
        return pushUserNum;
    }

    public static void setPushUserNum(Object pushUserNum) {
        UserInfo.pushUserNum = pushUserNum;
    }


    public static String getNeedUpdate() {
        return needUpdate;
    }

    public static void setNeedUpdate(String needUpdate) {
        UserInfo.needUpdate = needUpdate;
    }

    public static int getNotice_1_num() {
        return notice_1_num;
    }

    public static void setNotice_1_num(int notice_1_num) {
        UserInfo.notice_1_num = notice_1_num;
    }

    public static int getNotice_2_num() {
        return notice_2_num;
    }

    public static void setNotice_2_num(int notice_2_num) {
        UserInfo.notice_2_num = notice_2_num;
    }

    public static int getNotice_3_num() {
        return notice_3_num;
    }

    public static void setNotice_3_num(int notice_3_num) {
        UserInfo.notice_3_num = notice_3_num;
    }

    public static int getRefManagerNum() {
        return refManagerNum;
    }

    public static void setRefManagerNum(int refManagerNum) {
        UserInfo.refManagerNum = refManagerNum;
    }

    public static String getPwNotSecret() {
        return pwNotSecret;
    }

    public static void setPwNotSecret(String pwNotSecret) {
        UserInfo.pwNotSecret = pwNotSecret;
    }

    public static String getRefManagerCount() {
        return refManagerCount;
    }

    public static void setRefManagerCount(String refManagerCount) {
        UserInfo.refManagerCount = refManagerCount;
    }

    public static String getManagerRate() {
        return managerRate;
    }

    public static void setManagerRate(String managerRate) {
        UserInfo.managerRate = managerRate;
    }

    public static String getTeamLeaderRate() {
        return teamLeaderRate;
    }

    public static void setTeamLeaderRate(String teamLeaderRate) {
        UserInfo.teamLeaderRate = teamLeaderRate;
    }

    public static String getPlatformRate() {
        return platformRate;
    }

    public static void setPlatformRate(String platformRate) {
        UserInfo.platformRate = platformRate;
    }

    public static String getRefManagerGrade() {
        return refManagerGrade;
    }

    public static void setRefManagerGrade(String refManagerGrade) {
        UserInfo.refManagerGrade = refManagerGrade;
    }

    public static String getPushAll() {
        return pushAll;
    }

    public static void setPushAll(String pushAll) {
        UserInfo.pushAll = pushAll;
    }

    public static String getPushAuction() {
        return pushAuction;
    }

    public static void setPushAuction(String pushAuction) {
        UserInfo.pushAuction = pushAuction;
    }

    public static String getPushNotice() {
        return pushNotice;
    }

    public static void setPushNotice(String pushNotice) {
        UserInfo.pushNotice = pushNotice;
    }

    public static String getPushMarketing() {
        return pushMarketing;
    }

    public static void setPushMarketing(String pushMarketing) {
        UserInfo.pushMarketing = pushMarketing;
    }

    public static String getUserDOB() {
        return userDOB;
    }

    public static void setUserDOB(String userDOB) {
        UserInfo.userDOB = userDOB;
    }

    public static String getUserBankName() {
        return userBankName;
    }

    public static void setUserBankName(String userBankName) {
        UserInfo.userBankName = userBankName;
    }

    public static String getUserBankOwner() {
        return userBankOwner;
    }

    public static void setUserBankOwner(String userBankOwner) {
        UserInfo.userBankOwner = userBankOwner;
    }

    public static String getRefManagerID() {
        return refManagerID;
    }

    public static void setRefManagerID(String refManagerID) {
        UserInfo.refManagerID = refManagerID;
    }

    public static String getMemberName() {
        return memberName;
    }

    public static void setMemberName(String memberName) {
        UserInfo.memberName = memberName;
    }

    public static String getLoanNum() {
        return loanNum;
    }

    public static void setLoanNum(String loanNum) {
        UserInfo.loanNum = loanNum;
    }

    public static String getAuctionNumShort() {
        return auctionNumShort;
    }

    public static void setAuctionNumShort(String auctionNumShort) {
        UserInfo.auctionNumShort = auctionNumShort;
    }

    public static String getAutoLogin() {
        return autoLogin;
    }

    public static void setAutoLogin(String autoLogin) {
        UserInfo.autoLogin = autoLogin;
    }

    public static String getLatestDealPrice() {
        return LatestDealPrice;
    }

    public static void setLatestDealPrice(String latestDealPrice) {
        LatestDealPrice = latestDealPrice;
    }

    public static String getHopePrice() {
        return hopePrice;
    }

    public static void setHopePrice(String hopePrice) {
        UserInfo.hopePrice = hopePrice;
    }

    public static String getAddr() {
        return addr;
    }

    public static void setAddr(String addr) {
        UserInfo.addr = addr;
    }

    public static String getTeamMemberNum_as_leader() {
        return teamMemberNum_as_leader;
    }

    public static void setTeamMemberNum_as_leader(String teamMemberNum_as_leader) {
        UserInfo.teamMemberNum_as_leader = teamMemberNum_as_leader;
    }

    public static String getTeamMemberName_as_leader() {
        return teamMemberName_as_leader;
    }

    public static void setTeamMemberName_as_leader(String teamMemberName_as_leader) {
        UserInfo.teamMemberName_as_leader = teamMemberName_as_leader;
    }


    public static int getRefMemberNum() {
        return refMemberNum;
    }

    public static void setRefMemberNum(int refMemberNum) {
        UserInfo.refMemberNum = refMemberNum;
    }

    public static String getUserLicenseNum() {
        return userLicenseNum;
    }

    public static void setUserLicenseNum(String userLicenseNum) {
        UserInfo.userLicenseNum = userLicenseNum;
    }

    public static String getUserBankNum() {
        return userBankNum;
    }

    public static void setUserBankNum(String userBankNum) {
        UserInfo.userBankNum = userBankNum;
    }

    public static String getHasCardImage() {
        return hasCardImage;
    }

    public static void setHasCardImage(String hasCardImage) {
        UserInfo.hasCardImage = hasCardImage;
    }

    public static int getUserNum() {
        return userNum;
    }

    // public static 리턴 타입(자료형 또는 void) 메소드명(자료형 변수명),
    // UserInfo는 객체, setUserNum은 메소드, int는 입력자료형, userNum은 입력변수,  userNum은 상단에서 전역변수로 선언함
    public static void setUserNum(int userNum) {
        UserInfo.userNum = userNum;
    }

    public static int getUserGrade() {
        return userGrade;
    }

    public static void setUserGrade(int userGrade) {
        UserInfo.userGrade = userGrade;

    }

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        UserInfo.userID = userID;
    }


    public static String getUserPassword() {
        return userPassword;
    }

    public static void setUserPassword(String userPassword) {
        UserInfo.userPassword = userPassword;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        UserInfo.userName = userName;
    }

    public static String getUserPhone() {
        return userPhone;
    }

    public static void setUserPhone(String userPhone) {
        UserInfo.userPhone = userPhone;
    }

    public static void setIsLogin(boolean isLogin) {
        UserInfo.isLogin = isLogin;
    }

    public static boolean getIsLogin() {
        return isLogin; //get으로 변수의 isLogin을 받아오고, set로 true, false를 구분한다.
    }

    public static int getIsUpload() {
        return isUpload;
    }

    public static void setIsUpload(int isUpload) {
        UserInfo.isUpload = isUpload;
    }


    public static void LogOut() {
        UserInfo.setIsLogin(false);
        UserInfo.setUserPhone("");
        UserInfo.setUserName("");
        UserInfo.setUserGrade(0);
        UserInfo.setUserNum(0);
        UserInfo.setUserID("");
        UserInfo.setUserBankNum("");
        UserInfo.setHasCardImage("");
        UserInfo.setUserLicenseNum("");
        UserInfo.setAutoLogin("n");

        Log.e("로그아웃 ", "메소드 진입");
    }

}
