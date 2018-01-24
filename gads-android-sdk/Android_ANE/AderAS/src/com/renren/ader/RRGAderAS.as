package com.renren.ader
{
	import flash.events.DataEvent;
	import flash.events.EventDispatcher;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	
	import mx.core.IFactory;
	

	public class RRGAderAS extends EventDispatcher
	{
		private static var _aderExtContext:ExtensionContext=null;
		private static var _aderASInstance:RRGAderAS=null;
		//banner广告相关回调事件
		/**获取广告成功*/
		public  static var RRG_ADER_SUCCESS_EVENT:String = "RRG_ADER_SUCCESS_EVENT";
		/**SDK返回的错误报告*/
		public  static var RRG_ADER_FAIL_EVENT:String = "RRG_ADER_FAIL_EVENT";
		/**将要弹出新的视图*/
		public  static var RRG_ADER_WILL_PRESENT_VIEWCONTROLLER_EVENT:String = "RRG_ADER_WILL_PRESENT_VIEWCONTROLLER_EVENT";
		/**Dismiss弹出的视图*/
		public  static var RRG_ADER_DID_DISMISS_VIEWCONTROLLER_EVENT:String = "RRG_ADER_DID_DISMISS_VIEWCONTROLLER_EVENT";
		//插屏广告相关回调事件
		/**插屏广告请求成功，可以显示*/
		public  static var RRG_ADER_INTERSTITIAL_DID_RECEIVE_AD_EVENT:String = "RRG_ADER_INTERSTITIAL_DID_RECEIVE_AD_EVENT";
		/**插屏广告请求发生异常*/
		public  static var RRG_ADER_INTERSTITIAL_FAIL_EVENT:String = "RRG_ADER_INTERSTITIAL_FAIL_EVENT";
		/**将要显示插屏广告*/
		public  static var RRG_ADER_INTERSTITIAL_WILL_PRESENT_EVENT:String = "RRG_ADER_INTERSTITIAL_WILL_PRESENT_EVENT";
		/**将要移除插屏广告*/
		public  static var RRG_ADER_INTERSTITIAL_WILL_DISMISS_EVENT:String = "RRG_ADER_INTERSTITIAL_WILL_DISMISS_EVENT";
		/**插屏广告已被移除*/
		public  static var RRG_ADER_INTERSTITIAL_DID_DISMISS_EVENT:String = "RRG_ADER_INTERSTITIAL_DID_DISMISS_EVENT";
		//插屏广告尺寸
		public static var ADER_INTERSTITIALAD_SIZE_50:int =2;     
		public static var ADER_INTERSTITIALAD_SIZE_75:int =1;
		public static var ADER_INTERSTITIALAD_SIZE_100:int =0;
		
		public function RRGAderAS(){
			initExtContext();
		}
		/*
		*实例获取接口 
		*/
		public static function get instance():RRGAderAS
		{
			if(_aderASInstance==null)
			{
				_aderASInstance=new RRGAderAS();
			}
			return _aderASInstance;
		}
		
		/*
		 *初始化本机扩展实例 
		*/
		private function initExtContext():Boolean{
			if(_aderExtContext==null)
			{
				_aderExtContext=ExtensionContext.createExtensionContext("com.renren.ader",null);
				if(_aderExtContext)
				{
					_aderExtContext.addEventListener(StatusEvent.STATUS,onStatus);
				}
			}
			return (_aderExtContext!=null);
		}
		
		/*
		 *开启广告服务 
		*/
		public function startAder(appid:String,xMargin:int,yMargin:int,width:int,height:int,mode:int):void{
			if(initExtContext()&&appid!=null)
			{
				_aderExtContext.call("RRGAder_start",appid,xMargin,yMargin,width,height,mode);
			}
		}
		
		/*
		*移动广告视图 
		*/
		public function moveAder(xMargin:int,yMargin:int):void{
			if(initExtContext())
			{
				_aderExtContext.call("RRGAder_move",xMargin,yMargin);
			}
		}
		
		/*
		*停止广告服务 
		*/
		public function stopAder():void{
			if(initExtContext())
			{
				_aderExtContext.call("RRGAder_stop");
			}
		}
		
		/*
		*获取设备信息 
		*/
		public function getDeviceType():String{
			if(initExtContext())
			{
			    var devicetype:String=	_aderExtContext.call("RRGAder_DeviceType") as String;
				if(devicetype!=null)
				{
					return devicetype;
				}
			}
			return "null";
		}
		
		/*
		*初始化插屏广告 
		*/
		public function initInterstitialAd(appid:String,adSize:int,mode:int, isApplyLBS:Boolean):void{
			if(initExtContext()&&appid!=null)
			{
				_aderExtContext.call("RRGAder_initInterstitialAd",appid,adSize,mode,isApplyLBS);
			}
		}
		
		/*
		*请求插屏广告
		*/
		public function requestInterstitialAd():void{
			if(initExtContext())
			{
				 _aderExtContext.call("RRGAder_requestInterstitialAd");
			}
		}
		
		/*
		*查询插屏广告是否就绪
		*/
		public function isInterstitialAdReady():Boolean{
			if(initExtContext())
			{
				return _aderExtContext.call("RRGAder_isInterstitialAdReady") as Boolean;
			}
			return false;
		}
		
		/*
		*显示插屏广告视图
		*/
		public function presentInterstitialAd():void{
			if(initExtContext())
			{
			  _aderExtContext.call("RRGAder_presentInterstitialAd");
			}
		}
		
		/*
		*返回sdk版本号
		*/
		public function getSDKVersion():String{
			if(initExtContext())
			{
				return _aderExtContext.call("RRGAder_getSDKVersion") as String;
			}
			return null;
		}
		
		/*
		*获取到ios设备的汇报事件反馈 
		*/
		private function onStatus(event:StatusEvent):void
		{
			//if(event.code ==RRGAderAS.RRG_ADER_SUCCESS_EVENT||event.code ==RRGAderAS.RRG_ADER_FAIL_EVENT)
			var ecode:String=event.code;
			if(ecode!=null)
			{
				var datamsg:DataEvent=new DataEvent(ecode);
				datamsg.data=event.level;
				this.dispatchEvent(datamsg);
			}
		}
		
		public function dispose():void
		{
			if(_aderExtContext !=null){
				_aderExtContext.dispose();
				_aderExtContext = null;
			}
		}
		
	}
}