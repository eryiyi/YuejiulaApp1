package com.liangxun.yuejiula.base;

/**
 * Created by zhanghl on 2015/1/12.
 */
public class InternetURL {
    //mob
    public static final String APP_MOB_KEY = "12bd04f0dc118";
    public static final String APP_MOB_SCRECT = "6701d0f6ac951ba0b1f38af6aa34e5c3";

//    public static final String INTERNAL = "http://192.168.0.224:8080/";
    public static final String INTERNAL = "http://120.27.41.21:8070/";
    //多媒体文件上传接口
//    public static final String UPLOAD_FILE = INTERNAL + "uploadImage.do";
    public static final String QINIU_URL = "http://7xt74j.com1.z0.glb.clouddn.com/";

    public static final String UPLOAD_TOKEN = INTERNAL + "token.json";
    //登陆
    public static final String LOGIN_URL = INTERNAL + "memberLogin.do";
    //动态
    public static final String RECORD_URL = INTERNAL + "recordList.do";
    //获得二手市场类别
    public static final String GET_GOODSTYPE_URL = INTERNAL + "goodsTypeList.do";
    //赞动态
    public static final String CLICK_LIKE_URL = INTERNAL + "zanRecord.do";
    //注册
    public static final String REGIST_END_URL = INTERNAL + "memberRegister.do";
    //查询广告位
    public static final String GET_BIGAD_URL = INTERNAL + "getBigAdvert.do";
    //查询小广告位
    public static final String GET_SMALLAD_URL = INTERNAL + "getSmallAdvert.do";
    //查询动态评论
    public static final String GET_DETAIL_PL_URL = INTERNAL + "getCommentsByRecord.do";
    //添加说说
    public static final String PUBLIC_MOOD_URL = INTERNAL + "sendRecord.do";
    //添加评论
    public static final String PUBLISH_COMMENT_RECORD = INTERNAL + "saveComment.do";
    //查询赞
    public static final String GET_FAVOUR_URL = INTERNAL + "listZan.do";
    //发布商品
    public static final String PUBLISH_GOODS_URL = INTERNAL + "addGoods.do";
    //删除商品
    public static final String DELETE_GOODS_URL = INTERNAL + "paopaogoods/delete.do";
    //查询告示
    public static final String GET_NOTICE_URL = INTERNAL + "appListNotice.do";
    //告示详情
    public static final String GET_NOTICE_DETAIL_URL = INTERNAL + "viewNotice.do";
    //获取微兼职类别
    public static final String GET_PARTTIMETYPE_URL = INTERNAL + "appListPartTimeType.do";
    //获取兼职
    public static final String GET_PARTTIME_URL = INTERNAL + "listPartTime.do";
    //获取兼职详情
    public static final String GET_PARTTIME_DETAIL_URL = INTERNAL + "viewPartTime.do";
    //发布兼职
    public static final String PUBLISH_PARTTIME_URL = INTERNAL + "savePartTime.do";
    //删除兼职
    public static final String DELETE_PARTTIME_URL = INTERNAL + "deletePartTime.do";
    //与我相关
    public static final String ANDME_URL = INTERNAL + "listRelate.do";
    //根据动态uuid查询动态详情
    public static final String GET_RECORD_DETAIL_BYUUID_URL = INTERNAL + "getRecordById.do";
    //根据商品UUID查询商品详情
    public static final String GET_GOODS_DETAIL_BYUUID_URL = INTERNAL + "paopaogoods/findById.do";
    //根据兼职UUID查询兼职详情
    public static final String GET_PART_DETAIL_BYUUID_URL = INTERNAL + "findGoodsById.do";
    //查询商品评论
    public static final String GET_GOODS_COMMENT_URL = INTERNAL + "listGoodsComment.do";
    //添加商品评论
    public static final String PUBLISH_GOODS_COMMNENT_URL = INTERNAL + "saveGoodsComment.do";
    //根据用户UUid查询用户信息
    public static final String GET_EMP_DETAIL_URL = INTERNAL + "getMemberInfoById.do";
    //推送地址
    public static final String UPDATE_PUSH_ID = INTERNAL + "updatePushId.do";
    //重置密码
    public static final String FIND_PWR_TWO = INTERNAL + "resetPassword.do";
    //修改资料
    public static final String UPDATE_PROFILE_URL = INTERNAL + "modifyMember.do";
    //添加举报
    public static final String ADD_REPORT_URL = INTERNAL + "report.do";
    //查询举报列表
    public static final String GET_REPORT_URL = INTERNAL + "listReport.do";
    //查询禁闭列表
    public static final String GET_JINBI_URL = INTERNAL + "listCloseMember.do";
    //跟新举报
    public static final String CANCLE_REPORT_URL = INTERNAL + "manageReport.do";
    //关禁闭
    public static final String REPORT_JINBI_TIME_URL = INTERNAL + "saveManagerEmp.do";
    //查询好友资料
    public static final String GET_FRIENDS_URL = INTERNAL + "listMemberInfo.do";
    //获取省份列表
    public static final String GET_PROVINCE_URL = INTERNAL + "getProvince.do";
    //获取大学列表
    public static final String GET_COLLEAGE_URL = INTERNAL + "getCollege.do";
    //修改密码
    public static final String UPDATE_PWR_URL = INTERNAL + "resetPass.do";
    //修改手机号
    public static final String UPDATE_MOBILE_URL = INTERNAL + "resetMobile.do";
    //加好友
    public static final String SEARCH_CONTACT = INTERNAL + "searchMember.do";
    //查询好友资料
    public static final String GET_INVITE_CONTACT_URL = INTERNAL + "listInviteMemberInfo.do";
    //删除禁闭
    public static final String DELETE_JINBI_URL = INTERNAL + "openManagerEmp.do";
    //获取所有新闻类别
    public static final String GET_NEWS_TYPE_URL = INTERNAL + "listNewsTypeApp.do";
    //根据新闻类别huoq新闻
    public static final String GET_NEWS_URL = INTERNAL + "listNewsApp.do";
    //获取新闻详情  根据新闻UUID
    public static final String GET_NEWS_DETAIL_URL = INTERNAL + "viewNews.do";
    //发布新闻 手机端
    public static final String PUBLISH_NEWS_URL = INTERNAL + "saveNewsApp.do";
    //删除新闻
    public static final String DELETE_NEWS_UUID = INTERNAL + "deleteNews.do";
    //根据动态Id删除动态
    public static final String DELETE_RECORDS_URL = INTERNAL + "deleteRecordById.do";
    //share  动态viewRecord.do 根据ID查看动态
    public static final String SHARE_RECORD = INTERNAL + "viewRecord.do";

    //share viewGoods.do 根据ID查看商品展示
    public static final String SHARE_GOODS = INTERNAL + "viewGoods.do";
    //根据承包商的会员ID查找该承包商的商家
    public static final String GET_SHANGJIAS_URL = INTERNAL + "findSellerById.do";
    //设置商家setSeller.do
    public static final String SET_SHAGNJIA_URL = INTERNAL + "setSeller.do";
    // 查找经销商开通了几个学校
    public static final String GET_SCHOOLS_BY_JXS_URL = INTERNAL + "findContractSchoolById.do";
    //查询商家的学校
    public static final String GET_SHOOLS_BYSJ_URL = INTERNAL + "findSchoolList.do";
    //删除商家
    public static final String DELETE_SHAGNJIA_URL = INTERNAL + "deleteSeller.do";
    // 给商家更新到期时间updateSellerGoods.do
    public static final String UPDATE_SHAGNJIA_URL = INTERNAL + "updateSellerGoods.do";
    //查询所有的承包商
    public static final String SEARCH_SJS_URL = INTERNAL + "getAllContract.do";
    //置顶  取消置顶商品
    public static final String UPDATE_GOODS_TOP_URL = INTERNAL + "updateGoodsPosition.do";
    //根据代理商UUID查询推广信息
    public static final String TUIGUANG_AD_URL = INTERNAL + "getPromotion.do";
    //删除推广
    public static final String DELETE_TUIGUANG_AD_URL = INTERNAL + "deletePromotion.do";
    //添加用户到群组
    public static final String ADD_USER_TO_GROUP = INTERNAL + "addUserToGroup.do";
    //获取随机的十个用户
    public static final String RANDOM_GET_TEN_USERS = INTERNAL + "searchRecommend.do";
    //查询最新的PK
    public static final String GET_NEW_PK_URL = INTERNAL + "listWorkApp.do";
    //查询PK评论
    public static final String GET_DETAIL_PK_PL_URL = INTERNAL + "listPkComment.do";
    //查询赞
    public static final String GET_PK_FAVOUR_URL = INTERNAL + "listPkZanApp.do";
    //投票
    public static final String PK_CLICK_LIKE_URL = INTERNAL + "addPkZanApp.do";
    //添加评论
    public static final String PK_PUBLISH_COMMENT_RECORD = INTERNAL + "addPkComment.do";
    //主题查询
    public static final String PK_GET_TITLE_URL = INTERNAL + "getTheme.do";
    //查询奖品
    public static final String PK_GET_PRISIZE_URL = INTERNAL + "getTheme.do";
    //发作品
    public static final String PK_ADD_ZUOPIN_URL = INTERNAL + "savePkWork.do";
    //删除作品
    public static final String PK_DELETE_ZP_URL = INTERNAL + "deleteWorkById.do";
    //查询冠军榜
    public static final String PK_GET_CHAMPION = INTERNAL + "getChampion.do";
    //添加奖品--代理商
    public static final String PK_ADD_PRIZE_URL = INTERNAL + "savePrize.do";
    //根据承包商ID查找所有设置的奖品 + 查询 主题的奖品
    public static final String PK_GET_PRIZES_URL = INTERNAL + "viewPriceApp.do";
    //根据id删除奖品
    public static final String PK_DELETE_URL = INTERNAL + "deletePrize.do";
    //查询所有往期主题
    public static final String PK_GET_WANGQI = INTERNAL + "listThemeApp.do";
    //根据商家会员ID查找所有的学校
    public static final String GET_SCHOOL_BYSJUUID = INTERNAL + "findSchoolListByEmp.do";
    //确认领奖
    public static final String SURE_PRIZES_URL = INTERNAL + "championSure.do";
    //参赛规则
    public static final String Guize_paopao_URL = INTERNAL + "/html/guize.html";
    //根据作品ID查找作品findWorkById.do
    public static final String GET_PK_DETAIL_BYUUID = INTERNAL + "findWorkById.do";
    //pk分享
    public static final String GET_VIEW_PK = INTERNAL + "viewWork.do";

    //收藏商品接口
    public static final String SAVE_FAVOUR = INTERNAL + "saveGoodsFavour.do";
    //收藏商品列表
    public static final String MINE_FAVOUR = INTERNAL + "listFavour.do";
    //删除收藏的商品
    public static final String DELETE_FAVOUR = INTERNAL + "deleteFavour.do";

    //我的收货地址列表
    public static final String MINE_ADDRSS = INTERNAL + "listShoppingAddress.do";
    //添加收货地址
    public static final String ADD_MINE_ADDRSS = INTERNAL + "saveShoppingAddress.do";
    //更新收货地址
    public static final String UPDATE_MINE_ADDRSS = INTERNAL + "updateShoppingAddress.do";
    //删除收货地址
    public static final String DELETE_MINE_ADDRSS = INTERNAL + "deleteShoppingAddress.do";
    //获得默认收货地址
    public static final String GET_MOREN_ADDRESS = INTERNAL + "getSingleAddressByEmpId.do";
    //传订单给服务端--生成订单
    public static final String SEND_ORDER_TOSERVER = INTERNAL + "orderSave.do";
    //更新订单状态
    public static final String UPDATE_ORDER_TOSERVER = INTERNAL + "orderUpdate.do";
    //查询订单列表
    public static final String MINE_ORDERS_URL = INTERNAL + "listOrders.do";
    //根据地址id，查询收货地址、
    public static final String GET_ADDRESS_BYID = INTERNAL + "getSingleAddressByAddressId.do";
    //更新订单
    public static final String UPDATE_ORDER = INTERNAL + "updateOrder.do";
    //去付款--单个订单付款
    public static final String SAVE_ORDER_SIGNLE = INTERNAL + "orderSaveSingle.do";
    //更新订单状态
    public static final String UPDATE_ORDER_TOSERVER_SINGLE = INTERNAL + "orderUpdateSingle.do";
    //添加收货地址--选择省份--城市--地区
    public static final String SELECT_PROVINCE_ADDRESS = INTERNAL + "appGetProvince.do";
    public static final String SELECT_CITY_ADDRESS = INTERNAL + "appGetCity.do";
    public static final String SELECT_AREA_ADDRESS = INTERNAL + "appGetArea.do";
    //查询订单数量
    public static final String SELECT_ORDER_NUM = INTERNAL + "selectOrderNum.do";
    //查询订单列表-商家
    public static final String MINE_ORDERS_SJ_URL = INTERNAL + "listOrdersMng.do";
    //卖家取消订单，说明取消原因
    public static final String CANCEL_ORDERS_SJ_URL = INTERNAL + "cancelOrderSave.do";
    //商城宝贝查询
    public static final String GET_GOODS_URL = INTERNAL + "paopaogoods/listGoods.do";
    //商城详细页
    public static final String DETAIL_GOODS_URL = INTERNAL + "paopaogoods/detail.do";
    public static final String SLIDENEWS_URL = INTERNAL + "viewpager/appList.do";
    //置顶取消置顶商品
    public static final String UPDATE_POSTION_GOODS = INTERNAL + "paopaogoods/updatePosition.do";
    //根据订单号查询订单状态
    public static final String FIND_ORDER_BYNO = INTERNAL + "findOrderByOrderNo.do";
    //分享新闻
    public static final String SHARE_NEWS_URL = INTERNAL + "viewNewsShare.do";
    //视频列表
    public static final String GET_VIDEOS_URL = INTERNAL + "listVideosApp.do";

    //视频分享
    public static final String SHARE_VIDEOS = INTERNAL + "viewVideos.do";

    //查询videos评论
    public static final String GET_VIDEOS_PL_URL = INTERNAL + "listVideosCommentApp.do";
    //查询videos赞
    public static final String GET_VIDEOS_FAVOUR_URL = INTERNAL + "appVideosListZan.do";

    //添加评论
    public static final String PUBLISH_VIDEO_COMMENT_RECORD = INTERNAL + "appVideosSaveComment.do";
    //添加赞
    public static final String PUBLISH_VIDEO_FAVOUR_RECORD = INTERNAL + "appVideoZanSave.do";

    //获得主题页面
    public static final String GET_THEME_URL = INTERNAL + "getThemeApp.do";


    public static final String MOB = INTERNAL + "getThemeApp.do";

    //获得心情标签
    public static final String GET_MOOD_URL = INTERNAL + "listsRecodMoods.do";

    //查询店铺信息liebiao
    public static final String GET_DIANPU_MSG_URL = INTERNAL + "appGetDianPu.do";
    //查询用户广告
    public static final String GET_DIANPU_ADS_URL = INTERNAL + "appGetAds.do";
    //查询第三方平台
    public static final String GET_THREE_PT_URL = INTERNAL + "appGetThreesBd.do";
    //查询店铺信息详情
    public static final String GET_DIANPU_MSG_DETAIL_URL = INTERNAL + "appGetProfileMsg.do";
    //更新产品上架 下架信息
    public static final String UPDATE_GOODS_STATUS_URL = INTERNAL + "paopaogoods/updatePaopaoGoodsJia.do";

    //发现网址
    public static final String FIND_WWW_URL = INTERNAL + "listFindsApp.do";
    //根据学校ID查询学校承包商
    public static final String GET_SCHOOL_MANAGER_BY_ID = INTERNAL + "getManagerById.do";

    //封号
    public static final String UPDATE_FENGHAO_URL = INTERNAL + "updateEmpIsFenghao.do";
    //封群
    public static final String UPDATE_FENGQUN_URL = INTERNAL + "updateEmpIsFengQun.do";

    //查询封号封群的承包商下的会员列表
    public static final String GET_FENGHAOFENGQUNS_URL = INTERNAL + "getFenghaofengquns.do";

    //竞价保存 查询  单个查询
    public static final String SAVE_RECORDJP_URL = INTERNAL + "saveRecordJp.do";
    public static final String GET_LISTS_RECORDJPS_URL = INTERNAL + "listRecordJps.do";
    public static final String GET_SINGLE_RECORDJP_URL = INTERNAL + "getSingleRecordJpById.do";


}
