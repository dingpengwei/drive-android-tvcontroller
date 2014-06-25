package com.goodow.android.drive.tvcontroller;

import com.goodow.realtime.android.AndroidPlatform;
import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.java.JavaWebSocket;
import com.goodow.realtime.store.Store;
import com.goodow.realtime.store.impl.StoreImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DriveAndroidModule extends AbstractModule {
  private static final String SERVER = "54.186.216.246:1986";

  static {
    AndroidPlatform.register();
    // adb shell setprop log.tag.JavaWebSocket DEBUG
    Logger.getLogger(JavaWebSocket.class.getName()).setLevel(Level.ALL);
  }

  @Override
  protected void configure() {
  }

  @Provides
  @Singleton
  Bus provideBus(Store store) {
    return store.getBus();
  }

  @Provides
  @Singleton
  Store provideStore() {
      return new StoreImpl(URL, Json.createObject().set(WebSocketBus.SESSION, DeviceInformationTools
              .getLocalMacAddressFromWifiInfo(contextProvider.get())));
  }
}