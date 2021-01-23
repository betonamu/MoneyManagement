package com.example.doanjava.common;

public class GlobalConst {

    //Table name in Firebase FireStore
    public final static String UsersTable = "Users";
    public final static String ExpensesTable = "Expenses";
    public final static String ExpenseCategoriesTable = "ExpenseCategories";
    public final static String LoanCategoriesTable = "LoanCategories";
    public final static String LoanTable = "Loans";

    //Url to upload file in Firebase FireStorage
    public final static String UrlUploadFileStorage = "gs://doanjava-843c8.appspot.com";

    public final static String DateMonthYearFormat = "dd/MM/yyyy";

    //Config title and content of daily notification
    public final static String AppTitle = "Money Management";
    public final static String ContentDailyNotification = "Mở ứng dụng và nhập các khoản chi tiêu cho hôm nay đi nào";

    //Time wake up device to display daily notification
    public final static int HourWakeUpDailyNotification = 21;
    public final static int MinuteWakeUpDailyNotification = 0;
    public final static int SecondsWakeUpDailyNotification = 0;

    public final static String AppVersion = "1.0";
    public final static String CreateBy = "Nhóm BBB Đại Học HUTECH";

    public final static String PageIdFacebook = "100008727483897";
    public final static String UrlBrowserFacebook = "https://www.facebook.com/";
    public final static String UrlFacebook = "fb://profile/";


    public final static String prefix = "VND ";
}
