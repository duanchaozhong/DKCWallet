package com.example.dell.dkcwallet.http;

import com.example.dell.dkcwallet.bean.FeeModel;
import com.example.dell.dkcwallet.bean.IsCheckModel;
import com.example.dell.dkcwallet.bean.LoginModel;
import com.example.dell.dkcwallet.bean.TokenModel;
import com.example.dell.dkcwallet.bean.TotalAssetsModel;
import com.example.dell.dkcwallet.bean.TradeRecordModel;
import com.example.dell.dkcwallet.bean.TransferDetailModel;
import com.example.dell.dkcwallet.bean.TransferModel;
import com.example.dell.dkcwallet.bean.TypeModel;
import com.example.dell.dkcwallet.bean.VerModel;
import com.example.dell.dkcwallet.bean.WalletListModel;
import com.example.dell.dkcwallet.bean.WalletModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 *
 * @author yiyang
 */
public interface ApiService {
//    Base URL：总是以/结尾
//    @GET/POST等：不要以/开头
    /**
     * 网络请求超时时间毫秒
     */
    int DEFAULT_TIMEOUT = 10000;
    int LONG_TIMEOUT = 60000;

    //测试地址
//    String BASE_URL = "http://192.168.2.110:8078/api/";
//    String BASE_URL = "http://192.168.2.58:8078/api/";
    String BASE_URL = "http://testapp.savinginvestment.biz/api/";

    //正式地址
//    String BASE_URL = "http://hzapp.savinginvestment.biz/api/";
//    String BASE_URL = "https://app.savinginvestment.biz/api/";

    String QR_CODE_URL = ApiService.BASE_URL + "financia/wallet/qrcode";

    /**
     *  发送验证码
     */
    @FormUrlEncoded
    @POST("code/email")
    Observable<HttpResult<String>> getCode(@Field("time") String time, @Field("loginname") String loginname);

    /**
     *
     * @param time  时间戳毫秒单位
     */
    @FormUrlEncoded
    @POST("user/login")
    Observable<HttpResult<LoginModel>> login(@Header("User-Agent") String agent, @Field("time") String time, @Field("code") String code, @Field
            ("loginname") String loginname, @Field("password") String password);
    /**
     *
     * @param time  时间戳毫秒单位
     */
    @FormUrlEncoded
    @POST("user/safeLogin")
    Observable<HttpResult<LoginModel>> safeLogin(@Header("User-Agent") String agent, @Field("time") String time, @Field("code") String code, @Field
            ("loginname") String loginname, @Field("password") String password);

    @POST("user/loginout")
    Observable<HttpResult<String>> loginOut();

    /**
     * 前端-[转账]-用户转账，获取转账详情
     * @param tradeNo   转账流水号
     * @param recordType   记录类型 目前只可查询 8 9 2
     */
    @FormUrlEncoded
    @POST("transferMoney/gettransfer")
    Observable<HttpResult<TransferDetailModel>> getTransfer(@Field("time") String time, @Field("tradeNo") String tradeNo, @Field("recordType") int recordType);

    /**
     *
     * @param currType  币种类型
     * @param type  转账类型，1转账，2出金到原力
     */
    @FormUrlEncoded
    @POST("transferMoney/getfee")
    Observable<HttpResult<FeeModel>> getFee(@Field("time") String time, @Field("currType") long currType, @Field("type") long type);
    @FormUrlEncoded
    @POST("transferMoney/gettype")
    Observable<HttpResult<TypeModel>> getType(@Field("time") String time, @Field("currType") long currType);

    /**
     * 前端-[转账]-用户转账
     * @param currType      币种类型
     * @param transferAddr  转账地址
     * @param amount        转账金额
     * @param remark        转账备注
     * @param type          转账类型
     * @param payPwd        交易密码
     * @return
     */
    @FormUrlEncoded
    @POST("transferMoney/transfer")
    Observable<HttpResult<TransferModel>> transfer(@Field("time") String time, @Field("currType") int currType, @Field
            ("transferAddr") String transferAddr, @Field("amount") String amount, @Field("remark") String remark,
                                                @Field("type") int type, @Field("payPwd") String payPwd);

    /**
     * 前端-[资产中心]-获取用户总资产折合
     */
    @FormUrlEncoded
    @POST("financia/wallet/getTotalAssets")
    Observable<HttpResult<TotalAssetsModel>> getTotalAssets(@Field("time") String time);

    /**
     * 前端->[资产中心->DKC钱包/BTC钱包]-获得用户交易记录
     * @param currType      币种类型
     * @param search        交易流水号
     * @param flowType      资金流动类型：0：流入，1：流出
     * @param beginAmount   起始金额
     * @param endAmount     结束金额
     * @param beginTime     起始时间 yyyy-MM-dd
     * @param endTime       结束时间 yyyy-MM-dd
     * @param date          初始查询为当前时间戳（毫秒），上拉刷新时填加最后一条createTime字段
     * @param id            初始查询为空，上拉刷新时添加最后一条id
     * @param limit         返回数据数
     * @return
     */
//    @FormUrlEncoded
//    @POST("financia/wallet/getUserTradeRecord")
//    Observable<HttpResult<TradeRecordModel>> getUserTradeRecord(@Field("time") String time, @Field("currType") long
//            currType, @Field("search") String search, @Field("flowType") Long flowType, @Field("beginAmount") Double
//            beginAmount, @Field("endAmount") Double endAmount, @Field("beginTime") String beginTime, @Field
//            ("endTime") String endTime, @Field("date") String date, @Field("id") Long id, @Field("limit") Integer limit);
    @FormUrlEncoded
    @POST("financia/wallet/getUserTradeRecord")
    Observable<HttpResult<TradeRecordModel>> getUserTradeRecord(@Field("time") String time, @Field("currType") long
            currType, @Field("search") String search, @Field("flowType") String flowType, @Field("beginAmount") String
            beginAmount, @Field("endAmount") String endAmount, @Field("beginTime") String beginTime, @Field
            ("endTime") String endTime, @Field("date") String date, @Field("id") Long id, @Field("limit") Integer limit);

    /**
     * 前端->[资产中心->DKC钱包/BTC钱包]-根据币种类型获得钱包资金和地址信息
     * @param currType  币种类型
     */
    @FormUrlEncoded
    @POST("financia/wallet/getWalletById")
    Observable<HttpResult<WalletModel>> getWalletById(@Field("time") String time, @Field("currType") long currType);

    /**
     * 前端-获取用户钱包地址信息
     */
    @FormUrlEncoded
    @POST("financia/wallet/get")
    Observable<HttpResult<WalletListModel>> walletGet(@Field("time") String time);

    /**
     * 前端-验证用户是否有该类型钱包
     */
    @FormUrlEncoded
    @POST("financia/wallet/isCheck")
    Observable<HttpResult<IsCheckModel>> walletCheck(@Field("time") String time, @Field("currType") Integer currType, @Field("transferAddr") String transferAddr);

    /**
     * 前端-钱包二维码
     */
    @FormUrlEncoded
    @POST("financia/wallet/qrcode")
    Observable<byte[]> walletQrCode(@Field("time") String time, @Field("currType") long currType);

    /**
     * 前端-新闻公告详情
     */
    @FormUrlEncoded
    @POST("webnotify/detail")
    Observable<HttpResult<LoginModel>> newsDetail(@Field("time") String time, @Field("id") String id);

    /**
     * 前端-新闻公告列表
     * @param type  类型，1网站公告 ，2媒体报道，3业内资讯
     * @param id    初始查询为空，上拉刷新时添加最后一条id
     * @param limit 返回数据数
     * @return
     */
    @FormUrlEncoded
    @POST("webnotify/list")
    Observable<HttpResult<LoginModel>> news(@Field("time") String time, @Field("type") int type, @Field("id")
            long id, @Field("limit") int limit);

    /**
     * @param platform 平台， 一般输入A
     */
    @FormUrlEncoded
    @POST("version/lastVersion")
    Observable<HttpResult<VerModel>> lastVersion(@Field("platform") String platform);

    @FormUrlEncoded
    @POST("token/addToken")
    Observable<HttpResult<TokenModel>> addToken(@Field("uuid") String uuid);
    @FormUrlEncoded
    @POST("token/deleteToken")
    Observable<HttpResult<LoginModel>> deleteToken(@Field("uuid") String uuid);
    @FormUrlEncoded
    @POST("token/loginToken")
    Observable<HttpResult<LoginModel>> loginToken(@Header("User-Agent") String agent, @Field("token") String token, @Field("uuid") String uuid);
    @FormUrlEncoded
    @POST("token/refreshToken")
    Observable<HttpResult<TokenModel>> refreshToken(@Field("token") String token, @Field("uuid") String uuid);
}
