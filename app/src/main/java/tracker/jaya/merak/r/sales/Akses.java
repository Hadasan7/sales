package tracker.jaya.merak.r.sales;

class Akses {
    static final String URL_LIST_DEPO="http://salestreck.nanoxy.co.id/api/ListDepo";
    static final String URL_LIST_ITEM="http://salestreck.nanoxy.co.id/api/ListItem";
    static final String URL_LIST_TASK_PEND="http://salestreck.nanoxy.co.id/api/taskPend";
    static final String URL_LIST_TASK_SEND="http://salestreck.nanoxy.co.id/api/taskSend";
    static final String URL_LIST_UPDATE_TASK="http://salestreck.nanoxy.co.id/api/updtTask";
    static final String URL_STORE_UPDATE="http://salestreck.nanoxy.co.id/api/storeUpdate";
    static final String URL_LIST_STORE_LOCATION="http://salestreck.nanoxy.co.id/api/ListStoreLocation";
    static final String URL_CEK_SALES="http://salestreck.nanoxy.co.id/api/CekSales";
    static final String URL_HISTORY_SALES="http://salestreck.nanoxy.co.id/api/HistorySales";
    static final String URL_LOKASI_SALES="http://salestreck.nanoxy.co.id/api/LokasiSales";
    static final String URL_TAMBAH_SALES="http://salestreck.nanoxy.co.id/api/TambahSales";
    static final String URL_TAMBAH_STORE="http://salestreck.nanoxy.co.id/api/TambahStore";
    static final String URL_FORGOT="http://salestreck.nanoxy.co.id/api/forgot";
    static final String URL_ORDER="http://salestreck.nanoxy.co.id/api/order";
    static final String URL_RESET="http://salestreck.nanoxy.co.id/api/setPassword";
    static final String URL_AKTIF_CODE="http://lib.rawzadigital.com/Aktivasi/Aktivasi.php";
    static final String URL_AKTIFKAN="http://lib.rawzadigital.com/Aktivasi/CekAktivasi.php";

    static final String TAG_JSON_ARRAY="result";

    static final String ListSales0="IDSales";
    static final String ListSales1="NamaLengkap";
    static final String ListSales2="UserName";
    static final String ListSales3="NoHp";
    static final String ListSales4="Email";
    static final String ListSales5="Password";
    static final String ListSales6="Depo";
    static final String ListSales8="Lokasi";
    static final String ListSales9="Waktu";

    static final String ListStore0="storeId";
    static final String ListStore1="storeName";
    static final String ListStore2="address";
    static final String ListStore3="location";
    static final String ListStore4="IDSales";
    static final String ListStore5="timestamp";
    static final String ListStore6="pemilik";
    static final String ListStore7="piutang";
    static final String ListStore8="kreditLimit";



    static final String ListTask0="sendId";
    static final String ListTask1="storeName";
    static final String ListTask2="qty";
    static final String ListTask3="status";
    static final String ListTask4="Lokasi";

    static final String ListDepo0="depoId";
    static final String ListDepo1="depoName";

    static final String ListAktif0="DiviceID";
    static final String ListAktif1="DiviceCode";
    static final String ListAktif2="AktivasiDevice";

    static final String ListForgot0="email";
    static final String ListForgot1="code";
    static final String ListForgot2="password";

    static final int LimitStore=650;

    static final String ListItem0="itemId";
    static final String ListItem1="nama";
    static final String ListItem2="harga";
    static final String ListItem3="itemName";
    static final String ListItem4="qty";
    static final String ListItem5="bill";

}
