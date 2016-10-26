package com.qcloud.weapp.test.tunnel;

import org.junit.*;
import static org.junit.Assert.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.Configuration;
import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.ConfigurationManager;
import com.qcloud.weapp.authorization.UserInfo;
import com.qcloud.weapp.test.HttpMock;
import com.qcloud.weapp.test.URLConnectionMock;
import com.qcloud.weapp.tunnel.EmitError;
import com.qcloud.weapp.tunnel.EmitResult;
import com.qcloud.weapp.tunnel.Tunnel;
import com.qcloud.weapp.tunnel.TunnelHandleOptions;
import com.qcloud.weapp.tunnel.TunnelHandler;
import com.qcloud.weapp.tunnel.TunnelMessage;
import com.qcloud.weapp.tunnel.TunnelRoom;
import com.qcloud.weapp.tunnel.TunnelService;

public class TunnelServiceTest {
	private TunnelServiceTestHelper helper = new TunnelServiceTestHelper();
	
	@Before
	public void setup() {
		Configuration config = new Configuration();
		config.setServerHost("test.qcloud.la");
		config.setAuthServerUrl("http://127.0.0.1:10086/auth");
		config.setTunnelServerUrl("http://127.0.0.1:10086/tunnel");
		config.setTunnelSignatureKey("test key");
		config.setNetworkTimeout(1);
		try {
			ConfigurationManager.setup(config);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetConnectionWithSession() {
		HttpMock httpMock = helper.createTunnelHttpMock("GET", "valid");
		TunnelHandler handlerMock = mock(TunnelHandler.class);

		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandleOptions options = new TunnelHandleOptions();
		options.setCheckLogin(true);
		try {
			service.handle(handlerMock, options);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject body = new JSONObject(httpMock.getResponseText());
			assertNotNull(body.getString("url"));
		} catch (JSONException e) {
			fail();
		}
		
		ArgumentCaptor<Tunnel> tunnel = ArgumentCaptor.forClass(Tunnel.class);
		ArgumentCaptor<UserInfo> userInfo = ArgumentCaptor.forClass(UserInfo.class);
		verify(handlerMock, only()).onTunnelRequest(tunnel.capture(), userInfo.capture());
		assertNotNull(tunnel.getValue());
		assertNotNull(userInfo.getValue());
	}
	

	@Test
	public void testGetConnectionWithoutSession() {
		HttpMock httpMock = helper.createTunnelHttpMock("GET");
		TunnelHandler handlerMock = mock(TunnelHandler.class);

		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandleOptions options = new TunnelHandleOptions();
		options.setCheckLogin(false);
		try {
			service.handle(handlerMock, options);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject body = new JSONObject(httpMock.getResponseText());
			assertNotNull(body.getString("url"));
		} catch (JSONException e) {
			fail(e.getMessage());
		}
		
		ArgumentCaptor<Tunnel> tunnel = ArgumentCaptor.forClass(Tunnel.class);
		ArgumentCaptor<UserInfo> userInfo = ArgumentCaptor.forClass(UserInfo.class);
		verify(handlerMock, only()).onTunnelRequest(tunnel.capture(), userInfo.capture());
		assertNotNull(tunnel.getValue());
		assertNull(userInfo.getValue());
	}
	

	@Test
	public void testGetConnectionWithInvalidSession() {
		HttpMock httpMock = helper.createTunnelHttpMock("GET", "invalid");
		TunnelHandler handlerMock = mock(TunnelHandler.class);

		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandleOptions options = new TunnelHandleOptions();
		options.setCheckLogin(true);
		try {
			service.handle(handlerMock, options);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject body = new JSONObject(httpMock.getResponseText());
			assertTrue(helper.checkBodyHasMagicId(body));
			assertNotNull(body.get("error"));
		} catch (JSONException e) {
			fail();
		}
		verify(handlerMock, never()).onTunnelRequest(any(Tunnel.class), any(UserInfo.class));
	}
	
	@Test
	public void testPostConnectPacket() throws JSONException, ConfigurationException {
		HttpMock httpMock = helper.createTunnelHttpMock("POST");
		httpMock.setRequestBody(helper.buildPacket(new JSONObject()
			.put("type", "connect")
			.put("tunnelId", "tunnel1")
			.toString()
		));
		
		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandler handlerMock = mock(TunnelHandler.class);
		
		service.handle(handlerMock, null);
		
		ArgumentCaptor<Tunnel> tunnel = ArgumentCaptor.forClass(Tunnel.class);
		verify(handlerMock, times(1)).onTunnelConnect(tunnel.capture());
		verify(handlerMock, only()).onTunnelConnect(tunnel.capture());
		assertEquals(tunnel.getValue().getTunnelId(), "tunnel1");
		assertTrue(helper.checkPostResponseSuccess(httpMock.getResponseText()));
	}
	

	@Test
	public void testPostClosePacket() throws JSONException, ConfigurationException {
		HttpMock httpMock = helper.createTunnelHttpMock("POST");
		httpMock.setRequestBody(helper.buildPacket(new JSONObject()
			.put("type", "close")
			.put("tunnelId", "tunnel1")
			.toString()
		));
		
		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandler handlerMock = mock(TunnelHandler.class);
		
		service.handle(handlerMock, null);
		
		ArgumentCaptor<Tunnel> tunnel = ArgumentCaptor.forClass(Tunnel.class);
		verify(handlerMock, times(1)).onTunnelClose(tunnel.capture());
		verify(handlerMock, only()).onTunnelClose(tunnel.capture());
		assertEquals(tunnel.getValue().getTunnelId(), "tunnel1");
		assertTrue(helper.checkPostResponseSuccess(httpMock.getResponseText()));
	}

	@Test
	public void testPostMessagePacket() throws JSONException, ConfigurationException {
		HttpMock httpMock = helper.createTunnelHttpMock("POST");
		httpMock.setRequestBody(helper.buildPacket(new JSONObject()
			.put("type", "message")
			.put("tunnelId", "tunnel1")
			.put("content", new JSONObject()
				.put("type", "test-type")
				.put("content", "test-content")
				.toString()
			)
			.toString()
		));
		
		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandler handlerMock = mock(TunnelHandler.class);
		
		service.handle(handlerMock, null);
		
		ArgumentCaptor<Tunnel> tunnel = ArgumentCaptor.forClass(Tunnel.class);
		ArgumentCaptor<TunnelMessage> message = ArgumentCaptor.forClass(TunnelMessage.class);
		verify(handlerMock, times(1)).onTunnelMessage(tunnel.capture(), message.capture());
		verify(handlerMock, only()).onTunnelMessage(tunnel.capture(), message.capture());
		assertEquals("tunnel1", tunnel.getValue().getTunnelId());
		assertEquals("test-type", message.getValue().getType());
		assertEquals("test-content", message.getValue().getContent());
		assertTrue(helper.checkPostResponseSuccess(httpMock.getResponseText()));
	}

	@Test
	public void testPostUnknownMessagePacket() throws JSONException, ConfigurationException {
		HttpMock httpMock = helper.createTunnelHttpMock("POST");
		httpMock.setRequestBody(helper.buildPacket(new JSONObject()
			.put("type", "message")
			.put("tunnelId", "tunnel1")
			.put("content", "unknown-raw")
			.toString()
		));
		
		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandler handlerMock = mock(TunnelHandler.class);
		
		service.handle(handlerMock, null);
		
		ArgumentCaptor<Tunnel> tunnel = ArgumentCaptor.forClass(Tunnel.class);
		ArgumentCaptor<TunnelMessage> message = ArgumentCaptor.forClass(TunnelMessage.class);
		verify(handlerMock, times(1)).onTunnelMessage(tunnel.capture(), message.capture());
		assertEquals("tunnel1", tunnel.getValue().getTunnelId());
		assertEquals("UnknownRaw", message.getValue().getType());
		assertEquals("unknown-raw", message.getValue().getContent());
		verifyNoMoreInteractions(handlerMock);
		assertTrue(helper.checkPostResponseSuccess(httpMock.getResponseText()));
	}
	
	@Test
	public void testPostUnknownPacket() throws JSONException, ConfigurationException {
		HttpMock httpMock = helper.createTunnelHttpMock("POST");
		httpMock.setRequestBody(helper.buildPacket(new JSONObject()
			.put("type", "unknown")
			.put("tunnelId", "tunnel1")
			.toString()
		));
		
		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandler handlerMock = mock(TunnelHandler.class);
		
		service.handle(handlerMock, null);
		
		Mockito.verifyZeroInteractions(handlerMock);
		assertTrue(helper.checkPostResponseSuccess(httpMock.getResponseText()));
	}
	
	@Test
	public void testPostPacketWithErrorSignature() throws JSONException, ConfigurationException {
		HttpMock httpMock = helper.createTunnelHttpMock("POST");
		httpMock.setRequestBody(helper.buildPacket(
			new JSONObject()
				.put("type", "connect")
				.put("tunnelId", "tunnel1")
				.toString(), 
			true
		));
		
		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandler handlerMock = mock(TunnelHandler.class);
		
		service.handle(handlerMock, null);
		
		Mockito.verifyZeroInteractions(handlerMock);
		assertFalse(helper.checkPostResponseSuccess(httpMock.getResponseText()));
	}
	

	@Test
	public void testPostBadRequest() throws JSONException, ConfigurationException {
		HttpMock httpMock = helper.createTunnelHttpMock("POST");
		httpMock.setRequestBody("illegal request");
		
		TunnelService service = new TunnelService(httpMock.request, httpMock.response);
		TunnelHandler handlerMock = mock(TunnelHandler.class);
		
		service.handle(handlerMock, null);
		
		Mockito.verifyZeroInteractions(handlerMock);
		assertFalse(helper.checkPostResponseSuccess(httpMock.getResponseText()));
	}
	
	@Test
	public void testTunnelEmit() throws JSONException, EmitError {
		URLConnectionMock mock = helper.useURLConnectionMock();
		try {
			mock.setResponseBody(new JSONObject().put("code", 0).toString());
			
			Tunnel tunnel = Tunnel.getById("tunnel1");
			tunnel.emit("test-type", "test-content");
			
			JSONArray packets = helper.resolvePackets(mock.getRequestBody());
			
			assertEquals(1, packets.length());
			
			JSONObject firstPacket = packets.getJSONObject(0);
			assertEquals("tunnel1", firstPacket.getJSONArray("tunnelIds").getString(0));
			assertEquals("message", firstPacket.getString("type"));
			
			JSONObject message = new JSONObject(firstPacket.getString("content"));
			assertEquals("test-type", message.getString("type"));
			assertEquals("test-content", message.getString("content"));
		} finally {
			helper.restoreURLConnectionMock();			 
		}
	}
	
	@Test
	public void testEmitWithInvalidTunnels() throws JSONException, EmitError {
		URLConnectionMock mock = helper.useURLConnectionMock();			
		try {
			mock.setResponseBody(new JSONObject()
				.put("code", 0)
				.put("data", new JSONObject() 
					.put("invalidTunnelIds", new JSONArray().put("tunnel1"))
					.toString()
				)
				.toString()
			);
			Tunnel tunnel = Tunnel.getById("tunnel1");
			EmitResult result = tunnel.emit("test-type", new JSONObject().put("message", "test-content"));
			assertEquals(1, result.getTunnelInvalidInfos().size());
			assertEquals("tunnel1", result.getTunnelInvalidInfos().get(0).getTunnelId());
		} finally {
			helper.restoreURLConnectionMock();
		}
	}
	
	@Test
	public void testTunnelClose() throws JSONException, EmitError {
		URLConnectionMock mock = helper.useURLConnectionMock();			
		try {
			mock.setResponseBody(new JSONObject().put("code", 0).toString());
			Tunnel tunnel = Tunnel.getById("tunnel1");
			tunnel.close();

			JSONArray packets = helper.resolvePackets(mock.getRequestBody());
			
			assertEquals(1, packets.length());
			assertEquals("tunnel1", packets.getJSONObject(0).getJSONArray("tunnelIds").getString(0));
			assertEquals("close", packets.getJSONObject(0).getString("type"));
		} finally {
			helper.restoreURLConnectionMock();
		}
	}
	
	@Test
	public void testRoomBroadcast() throws JSONException, EmitError {
		URLConnectionMock mock = helper.useURLConnectionMock();			
		try {
			mock.setResponseBody(new JSONObject().put("code", 0).toString());
			
			TunnelRoom room = new TunnelRoom();
			
			room.addTunnel(Tunnel.getById("tunnel1"));
			room.addTunnel(Tunnel.getById("tunnel2"));
			assertEquals(2, room.getTunnelCount());
			
			room.removeTunnelById("tunnel1");
			assertEquals(1, room.getTunnelCount());
			
			room.addTunnel(Tunnel.getById("tunnel3"));
            room.broadcast("test-type", "test-message");

			JSONArray packets = helper.resolvePackets(mock.getRequestBody());
			
			assertEquals(1, packets.length());
			
			JSONObject firstPacket = packets.getJSONObject(0);
			assertEquals(2, firstPacket.getJSONArray("tunnelIds").length());
			assertEquals("tunnel2", firstPacket.getJSONArray("tunnelIds").get(0));
			assertEquals("tunnel3", firstPacket.getJSONArray("tunnelIds").get(1));
			assertEquals("message", firstPacket.getString("type"));
			
			JSONObject message = new JSONObject(firstPacket.getString("content"));
			assertEquals("test-type", message.getString("type"));
			assertEquals("test-message", message.getString("content"));
		} finally {
			helper.restoreURLConnectionMock();
		}
	}
	

	@Test
	public void testRoomBroadcastWithInvalidTunnels() throws JSONException, EmitError {
		URLConnectionMock mock = helper.useURLConnectionMock();			
		try {
			mock.setResponseBody(new JSONObject()
				.put("code", 0)
				.put("data", new JSONObject()
					.put("invalidTunnelIds", new JSONArray().put("tunnel1").put("tunnel2"))
				)
				.toString()
			);
			
			TunnelRoom room = new TunnelRoom();
			
			room.addTunnel(Tunnel.getById("tunnel1"));
			room.addTunnel(Tunnel.getById("tunnel2"));
			room.addTunnel(Tunnel.getById("tunnel3"));
            EmitResult result = room.broadcast("test-type", "test-message");

            assertEquals(2, result.getTunnelInvalidInfos().size());
            assertEquals("tunnel1", result.getTunnelInvalidInfos().get(0).getTunnelId());
            assertEquals("tunnel2", result.getTunnelInvalidInfos().get(1).getTunnelId());
		} finally {
			helper.restoreURLConnectionMock();
		}
	}
}
