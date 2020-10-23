package org.wisdom.core;

public interface Header {
   byte[] getHashPrevBlock();
   long getNTime();
   long getnHeight();
}
