package com.espressif.iot.ui.esp;

/**
 * this class contains the MVC interface in Esp Component
 * 
 * the flow of the MVC is as follows:
 * 
 * Request:
 * user touch android device -> EspUIView: sendActionRequest() 
 * -> EspUIControl: receiveActionRequest() -> EspUIControl: sendUpdateRequest() 
 * -> EspUIModel: receiveUpdateRequest() 
 * -> EspUIModel's subclass will process and generate the response
 * 
 * Response:
 * EspUIModel: sendUpdateResponse() -> EspUIControl: receiveUpdateResponse()
 * -> EspUIControl: sendActionResponse() -> EspUIView: receiveActionResponse() 
 * -> EspUIView's subclass will process and update the UI for user
 * 
 * @author afunx
 *
 */

interface EspUIInterface{
	
}

interface EspUIControl extends EspUIInterface{
}

interface EspUIModel extends EspUIInterface{
}

interface EspUIView extends EspUIInterface{	
}