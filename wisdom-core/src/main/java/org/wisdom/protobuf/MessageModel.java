/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.protobuf;

public final class MessageModel {
  private MessageModel() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface PongMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.PongMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string magic = 1;</code>
     */
    java.lang.String getMagic();
    /**
     * <code>string magic = 1;</code>
     */
    com.google.protobuf.ByteString
        getMagicBytes();

    /**
     * <code>sfixed32 length = 2;</code>
     */
    int getLength();

    /**
     * <code>bytes checksum = 3;</code>
     */
    com.google.protobuf.ByteString getChecksum();

    /**
     * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
     */
    boolean hasPong();
    /**
     * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
     */
    MessageModel.PongMessage.Pong getPong();
    /**
     * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
     */
    MessageModel.PongMessage.PongOrBuilder getPongOrBuilder();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.PongMessage}
   */
  public  static final class PongMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.PongMessage)
      PongMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use PongMessage.newBuilder() to construct.
    private PongMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private PongMessage() {
      magic_ = "";
      checksum_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private PongMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              magic_ = s;
              break;
            }
            case 21: {

              length_ = input.readSFixed32();
              break;
            }
            case 26: {

              checksum_ = input.readBytes();
              break;
            }
            case 34: {
              MessageModel.PongMessage.Pong.Builder subBuilder = null;
              if (pong_ != null) {
                subBuilder = pong_.toBuilder();
              }
              pong_ = input.readMessage(MessageModel.PongMessage.Pong.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(pong_);
                pong_ = subBuilder.buildPartial();
              }

              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              MessageModel.PongMessage.class, MessageModel.PongMessage.Builder.class);
    }

    public interface PongOrBuilder extends
        // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.PongMessage.Pong)
        com.google.protobuf.MessageOrBuilder {

      /**
       * <code>bytes token = 1;</code>
       */
      com.google.protobuf.ByteString getToken();

      /**
       * <code>uint64 expires = 2;</code>
       */
      long getExpires();
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.PongMessage.Pong}
     */
    public  static final class Pong extends
        com.google.protobuf.GeneratedMessageV3 implements
        // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.PongMessage.Pong)
        PongOrBuilder {
    private static final long serialVersionUID = 0L;
      // Use Pong.newBuilder() to construct.
      private Pong(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
        super(builder);
      }
      private Pong() {
        token_ = com.google.protobuf.ByteString.EMPTY;
      }

      @java.lang.Override
      public final com.google.protobuf.UnknownFieldSet
      getUnknownFields() {
        return this.unknownFields;
      }
      private Pong(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        this();
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        int mutable_bitField0_ = 0;
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
            com.google.protobuf.UnknownFieldSet.newBuilder();
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 10: {

                token_ = input.readBytes();
                break;
              }
              case 16: {

                expires_ = input.readUInt64();
                break;
              }
              default: {
                if (!parseUnknownField(
                    input, unknownFields, extensionRegistry, tag)) {
                  done = true;
                }
                break;
              }
            }
          }
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(this);
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(
              e).setUnfinishedMessage(this);
        } finally {
          this.unknownFields = unknownFields.build();
          makeExtensionsImmutable();
        }
      }
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_Pong_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_Pong_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.PongMessage.Pong.class, MessageModel.PongMessage.Pong.Builder.class);
      }

      public static final int TOKEN_FIELD_NUMBER = 1;
      private com.google.protobuf.ByteString token_;
      /**
       * <code>bytes token = 1;</code>
       */
      public com.google.protobuf.ByteString getToken() {
        return token_;
      }

      public static final int EXPIRES_FIELD_NUMBER = 2;
      private long expires_;
      /**
       * <code>uint64 expires = 2;</code>
       */
      public long getExpires() {
        return expires_;
      }

      private byte memoizedIsInitialized = -1;
      @java.lang.Override
      public final boolean isInitialized() {
        byte isInitialized = memoizedIsInitialized;
        if (isInitialized == 1) return true;
        if (isInitialized == 0) return false;

        memoizedIsInitialized = 1;
        return true;
      }

      @java.lang.Override
      public void writeTo(com.google.protobuf.CodedOutputStream output)
                          throws java.io.IOException {
        if (!token_.isEmpty()) {
          output.writeBytes(1, token_);
        }
        if (expires_ != 0L) {
          output.writeUInt64(2, expires_);
        }
        unknownFields.writeTo(output);
      }

      @java.lang.Override
      public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1) return size;

        size = 0;
        if (!token_.isEmpty()) {
          size += com.google.protobuf.CodedOutputStream
            .computeBytesSize(1, token_);
        }
        if (expires_ != 0L) {
          size += com.google.protobuf.CodedOutputStream
            .computeUInt64Size(2, expires_);
        }
        size += unknownFields.getSerializedSize();
        memoizedSize = size;
        return size;
      }

      @java.lang.Override
      public boolean equals(final java.lang.Object obj) {
        if (obj == this) {
         return true;
        }
        if (!(obj instanceof MessageModel.PongMessage.Pong)) {
          return super.equals(obj);
        }
        MessageModel.PongMessage.Pong other = (MessageModel.PongMessage.Pong) obj;

        if (!getToken()
            .equals(other.getToken())) return false;
        if (getExpires()
            != other.getExpires()) return false;
        if (!unknownFields.equals(other.unknownFields)) return false;
        return true;
      }

      @java.lang.Override
      public int hashCode() {
        if (memoizedHashCode != 0) {
          return memoizedHashCode;
        }
        int hash = 41;
        hash = (19 * hash) + getDescriptor().hashCode();
        hash = (37 * hash) + TOKEN_FIELD_NUMBER;
        hash = (53 * hash) + getToken().hashCode();
        hash = (37 * hash) + EXPIRES_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getExpires());
        hash = (29 * hash) + unknownFields.hashCode();
        memoizedHashCode = hash;
        return hash;
      }

      public static MessageModel.PongMessage.Pong parseFrom(
          java.nio.ByteBuffer data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.PongMessage.Pong parseFrom(
          java.nio.ByteBuffer data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.PongMessage.Pong parseFrom(
          com.google.protobuf.ByteString data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.PongMessage.Pong parseFrom(
          com.google.protobuf.ByteString data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.PongMessage.Pong parseFrom(byte[] data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.PongMessage.Pong parseFrom(
          byte[] data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.PongMessage.Pong parseFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.PongMessage.Pong parseFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.PongMessage.Pong parseDelimitedFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input);
      }
      public static MessageModel.PongMessage.Pong parseDelimitedFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.PongMessage.Pong parseFrom(
          com.google.protobuf.CodedInputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.PongMessage.Pong parseFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }

      @java.lang.Override
      public Builder newBuilderForType() { return newBuilder(); }
      public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
      }
      public static Builder newBuilder(MessageModel.PongMessage.Pong prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }
      @java.lang.Override
      public Builder toBuilder() {
        return this == DEFAULT_INSTANCE
            ? new Builder() : new Builder().mergeFrom(this);
      }

      @java.lang.Override
      protected Builder newBuilderForType(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
      }
      /**
       * Protobuf type {@code org.ethereum.protobuf.PongMessage.Pong}
       */
      public static final class Builder extends
          com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
          // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.PongMessage.Pong)
          MessageModel.PongMessage.PongOrBuilder {
        public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
          return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_Pong_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
          return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_Pong_fieldAccessorTable
              .ensureFieldAccessorsInitialized(
                  MessageModel.PongMessage.Pong.class, MessageModel.PongMessage.Pong.Builder.class);
        }

        // Construct using MessageModel.PongMessage.Pong.newBuilder()
        private Builder() {
          maybeForceBuilderInitialization();
        }

        private Builder(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
          super(parent);
          maybeForceBuilderInitialization();
        }
        private void maybeForceBuilderInitialization() {
          if (com.google.protobuf.GeneratedMessageV3
                  .alwaysUseFieldBuilders) {
          }
        }
        @java.lang.Override
        public Builder clear() {
          super.clear();
          token_ = com.google.protobuf.ByteString.EMPTY;

          expires_ = 0L;

          return this;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
          return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_Pong_descriptor;
        }

        @java.lang.Override
        public MessageModel.PongMessage.Pong getDefaultInstanceForType() {
          return MessageModel.PongMessage.Pong.getDefaultInstance();
        }

        @java.lang.Override
        public MessageModel.PongMessage.Pong build() {
          MessageModel.PongMessage.Pong result = buildPartial();
          if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
          }
          return result;
        }

        @java.lang.Override
        public MessageModel.PongMessage.Pong buildPartial() {
          MessageModel.PongMessage.Pong result = new MessageModel.PongMessage.Pong(this);
          result.token_ = token_;
          result.expires_ = expires_;
          onBuilt();
          return result;
        }

        @java.lang.Override
        public Builder clone() {
          return super.clone();
        }
        @java.lang.Override
        public Builder setField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.setField(field, value);
        }
        @java.lang.Override
        public Builder clearField(
            com.google.protobuf.Descriptors.FieldDescriptor field) {
          return super.clearField(field);
        }
        @java.lang.Override
        public Builder clearOneof(
            com.google.protobuf.Descriptors.OneofDescriptor oneof) {
          return super.clearOneof(oneof);
        }
        @java.lang.Override
        public Builder setRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            int index, java.lang.Object value) {
          return super.setRepeatedField(field, index, value);
        }
        @java.lang.Override
        public Builder addRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.addRepeatedField(field, value);
        }
        @java.lang.Override
        public Builder mergeFrom(com.google.protobuf.Message other) {
          if (other instanceof MessageModel.PongMessage.Pong) {
            return mergeFrom((MessageModel.PongMessage.Pong)other);
          } else {
            super.mergeFrom(other);
            return this;
          }
        }

        public Builder mergeFrom(MessageModel.PongMessage.Pong other) {
          if (other == MessageModel.PongMessage.Pong.getDefaultInstance()) return this;
          if (other.getToken() != com.google.protobuf.ByteString.EMPTY) {
            setToken(other.getToken());
          }
          if (other.getExpires() != 0L) {
            setExpires(other.getExpires());
          }
          this.mergeUnknownFields(other.unknownFields);
          onChanged();
          return this;
        }

        @java.lang.Override
        public final boolean isInitialized() {
          return true;
        }

        @java.lang.Override
        public Builder mergeFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          MessageModel.PongMessage.Pong parsedMessage = null;
          try {
            parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            parsedMessage = (MessageModel.PongMessage.Pong) e.getUnfinishedMessage();
            throw e.unwrapIOException();
          } finally {
            if (parsedMessage != null) {
              mergeFrom(parsedMessage);
            }
          }
          return this;
        }

        private com.google.protobuf.ByteString token_ = com.google.protobuf.ByteString.EMPTY;
        /**
         * <code>bytes token = 1;</code>
         */
        public com.google.protobuf.ByteString getToken() {
          return token_;
        }
        /**
         * <code>bytes token = 1;</code>
         */
        public Builder setToken(com.google.protobuf.ByteString value) {
          if (value == null) {
    throw new NullPointerException();
  }

          token_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>bytes token = 1;</code>
         */
        public Builder clearToken() {

          token_ = getDefaultInstance().getToken();
          onChanged();
          return this;
        }

        private long expires_ ;
        /**
         * <code>uint64 expires = 2;</code>
         */
        public long getExpires() {
          return expires_;
        }
        /**
         * <code>uint64 expires = 2;</code>
         */
        public Builder setExpires(long value) {

          expires_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>uint64 expires = 2;</code>
         */
        public Builder clearExpires() {

          expires_ = 0L;
          onChanged();
          return this;
        }
        @java.lang.Override
        public final Builder setUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.setUnknownFields(unknownFields);
        }

        @java.lang.Override
        public final Builder mergeUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.mergeUnknownFields(unknownFields);
        }


        // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.PongMessage.Pong)
      }

      // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.PongMessage.Pong)
      private static final MessageModel.PongMessage.Pong DEFAULT_INSTANCE;
      static {
        DEFAULT_INSTANCE = new MessageModel.PongMessage.Pong();
      }

      public static MessageModel.PongMessage.Pong getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }

      private static final com.google.protobuf.Parser<Pong>
          PARSER = new com.google.protobuf.AbstractParser<Pong>() {
        @java.lang.Override
        public Pong parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new Pong(input, extensionRegistry);
        }
      };

      public static com.google.protobuf.Parser<Pong> parser() {
        return PARSER;
      }

      @java.lang.Override
      public com.google.protobuf.Parser<Pong> getParserForType() {
        return PARSER;
      }

      @java.lang.Override
      public MessageModel.PongMessage.Pong getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }

    }

    public static final int MAGIC_FIELD_NUMBER = 1;
    private volatile java.lang.Object magic_;
    /**
     * <code>string magic = 1;</code>
     */
    public java.lang.String getMagic() {
      java.lang.Object ref = magic_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        magic_ = s;
        return s;
      }
    }
    /**
     * <code>string magic = 1;</code>
     */
    public com.google.protobuf.ByteString
        getMagicBytes() {
      java.lang.Object ref = magic_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        magic_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int LENGTH_FIELD_NUMBER = 2;
    private int length_;
    /**
     * <code>sfixed32 length = 2;</code>
     */
    public int getLength() {
      return length_;
    }

    public static final int CHECKSUM_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString checksum_;
    /**
     * <code>bytes checksum = 3;</code>
     */
    public com.google.protobuf.ByteString getChecksum() {
      return checksum_;
    }

    public static final int PONG_FIELD_NUMBER = 4;
    private MessageModel.PongMessage.Pong pong_;
    /**
     * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
     */
    public boolean hasPong() {
      return pong_ != null;
    }
    /**
     * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
     */
    public MessageModel.PongMessage.Pong getPong() {
      return pong_ == null ? MessageModel.PongMessage.Pong.getDefaultInstance() : pong_;
    }
    /**
     * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
     */
    public MessageModel.PongMessage.PongOrBuilder getPongOrBuilder() {
      return getPong();
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getMagicBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, magic_);
      }
      if (length_ != 0) {
        output.writeSFixed32(2, length_);
      }
      if (!checksum_.isEmpty()) {
        output.writeBytes(3, checksum_);
      }
      if (pong_ != null) {
        output.writeMessage(4, getPong());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getMagicBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, magic_);
      }
      if (length_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeSFixed32Size(2, length_);
      }
      if (!checksum_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, checksum_);
      }
      if (pong_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, getPong());
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof MessageModel.PongMessage)) {
        return super.equals(obj);
      }
      MessageModel.PongMessage other = (MessageModel.PongMessage) obj;

      if (!getMagic()
          .equals(other.getMagic())) return false;
      if (getLength()
          != other.getLength()) return false;
      if (!getChecksum()
          .equals(other.getChecksum())) return false;
      if (hasPong() != other.hasPong()) return false;
      if (hasPong()) {
        if (!getPong()
            .equals(other.getPong())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + MAGIC_FIELD_NUMBER;
      hash = (53 * hash) + getMagic().hashCode();
      hash = (37 * hash) + LENGTH_FIELD_NUMBER;
      hash = (53 * hash) + getLength();
      hash = (37 * hash) + CHECKSUM_FIELD_NUMBER;
      hash = (53 * hash) + getChecksum().hashCode();
      if (hasPong()) {
        hash = (37 * hash) + PONG_FIELD_NUMBER;
        hash = (53 * hash) + getPong().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static MessageModel.PongMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.PongMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.PongMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.PongMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.PongMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.PongMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.PongMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.PongMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.PongMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static MessageModel.PongMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.PongMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.PongMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(MessageModel.PongMessage prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.PongMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.PongMessage)
        MessageModel.PongMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.PongMessage.class, MessageModel.PongMessage.Builder.class);
      }

      // Construct using MessageModel.PongMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        magic_ = "";

        length_ = 0;

        checksum_ = com.google.protobuf.ByteString.EMPTY;

        if (pongBuilder_ == null) {
          pong_ = null;
        } else {
          pong_ = null;
          pongBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return MessageModel.internal_static_org_ethereum_protobuf_PongMessage_descriptor;
      }

      @java.lang.Override
      public MessageModel.PongMessage getDefaultInstanceForType() {
        return MessageModel.PongMessage.getDefaultInstance();
      }

      @java.lang.Override
      public MessageModel.PongMessage build() {
        MessageModel.PongMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public MessageModel.PongMessage buildPartial() {
        MessageModel.PongMessage result = new MessageModel.PongMessage(this);
        result.magic_ = magic_;
        result.length_ = length_;
        result.checksum_ = checksum_;
        if (pongBuilder_ == null) {
          result.pong_ = pong_;
        } else {
          result.pong_ = pongBuilder_.build();
        }
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof MessageModel.PongMessage) {
          return mergeFrom((MessageModel.PongMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(MessageModel.PongMessage other) {
        if (other == MessageModel.PongMessage.getDefaultInstance()) return this;
        if (!other.getMagic().isEmpty()) {
          magic_ = other.magic_;
          onChanged();
        }
        if (other.getLength() != 0) {
          setLength(other.getLength());
        }
        if (other.getChecksum() != com.google.protobuf.ByteString.EMPTY) {
          setChecksum(other.getChecksum());
        }
        if (other.hasPong()) {
          mergePong(other.getPong());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        MessageModel.PongMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (MessageModel.PongMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object magic_ = "";
      /**
       * <code>string magic = 1;</code>
       */
      public java.lang.String getMagic() {
        java.lang.Object ref = magic_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          magic_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string magic = 1;</code>
       */
      public com.google.protobuf.ByteString
          getMagicBytes() {
        java.lang.Object ref = magic_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          magic_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder setMagic(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }

        magic_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder clearMagic() {

        magic_ = getDefaultInstance().getMagic();
        onChanged();
        return this;
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder setMagicBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

        magic_ = value;
        onChanged();
        return this;
      }

      private int length_ ;
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public int getLength() {
        return length_;
      }
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public Builder setLength(int value) {

        length_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public Builder clearLength() {

        length_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString checksum_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes checksum = 3;</code>
       */
      public com.google.protobuf.ByteString getChecksum() {
        return checksum_;
      }
      /**
       * <code>bytes checksum = 3;</code>
       */
      public Builder setChecksum(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        checksum_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes checksum = 3;</code>
       */
      public Builder clearChecksum() {

        checksum_ = getDefaultInstance().getChecksum();
        onChanged();
        return this;
      }

      private MessageModel.PongMessage.Pong pong_;
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.PongMessage.Pong, MessageModel.PongMessage.Pong.Builder, MessageModel.PongMessage.PongOrBuilder> pongBuilder_;
      /**
       * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
       */
      public boolean hasPong() {
        return pongBuilder_ != null || pong_ != null;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
       */
      public MessageModel.PongMessage.Pong getPong() {
        if (pongBuilder_ == null) {
          return pong_ == null ? MessageModel.PongMessage.Pong.getDefaultInstance() : pong_;
        } else {
          return pongBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
       */
      public Builder setPong(MessageModel.PongMessage.Pong value) {
        if (pongBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          pong_ = value;
          onChanged();
        } else {
          pongBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
       */
      public Builder setPong(
          MessageModel.PongMessage.Pong.Builder builderForValue) {
        if (pongBuilder_ == null) {
          pong_ = builderForValue.build();
          onChanged();
        } else {
          pongBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
       */
      public Builder mergePong(MessageModel.PongMessage.Pong value) {
        if (pongBuilder_ == null) {
          if (pong_ != null) {
            pong_ =
              MessageModel.PongMessage.Pong.newBuilder(pong_).mergeFrom(value).buildPartial();
          } else {
            pong_ = value;
          }
          onChanged();
        } else {
          pongBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
       */
      public Builder clearPong() {
        if (pongBuilder_ == null) {
          pong_ = null;
          onChanged();
        } else {
          pong_ = null;
          pongBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
       */
      public MessageModel.PongMessage.Pong.Builder getPongBuilder() {

        onChanged();
        return getPongFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
       */
      public MessageModel.PongMessage.PongOrBuilder getPongOrBuilder() {
        if (pongBuilder_ != null) {
          return pongBuilder_.getMessageOrBuilder();
        } else {
          return pong_ == null ?
              MessageModel.PongMessage.Pong.getDefaultInstance() : pong_;
        }
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage.Pong pong = 4;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.PongMessage.Pong, MessageModel.PongMessage.Pong.Builder, MessageModel.PongMessage.PongOrBuilder>
          getPongFieldBuilder() {
        if (pongBuilder_ == null) {
          pongBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              MessageModel.PongMessage.Pong, MessageModel.PongMessage.Pong.Builder, MessageModel.PongMessage.PongOrBuilder>(
                  getPong(),
                  getParentForChildren(),
                  isClean());
          pong_ = null;
        }
        return pongBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.PongMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.PongMessage)
    private static final MessageModel.PongMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new MessageModel.PongMessage();
    }

    public static MessageModel.PongMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<PongMessage>
        PARSER = new com.google.protobuf.AbstractParser<PongMessage>() {
      @java.lang.Override
      public PongMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new PongMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<PongMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<PongMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public MessageModel.PongMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface PingMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.PingMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string magic = 1;</code>
     */
    java.lang.String getMagic();
    /**
     * <code>string magic = 1;</code>
     */
    com.google.protobuf.ByteString
        getMagicBytes();

    /**
     * <code>sfixed32 length = 2;</code>
     */
    int getLength();

    /**
     * <code>bytes checksum = 3;</code>
     */
    com.google.protobuf.ByteString getChecksum();

    /**
     * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
     */
    boolean hasPing();
    /**
     * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
     */
    MessageModel.PingMessage.Ping getPing();
    /**
     * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
     */
    MessageModel.PingMessage.PingOrBuilder getPingOrBuilder();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.PingMessage}
   */
  public  static final class PingMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.PingMessage)
      PingMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use PingMessage.newBuilder() to construct.
    private PingMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private PingMessage() {
      magic_ = "";
      checksum_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private PingMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              magic_ = s;
              break;
            }
            case 21: {

              length_ = input.readSFixed32();
              break;
            }
            case 26: {

              checksum_ = input.readBytes();
              break;
            }
            case 34: {
              MessageModel.PingMessage.Ping.Builder subBuilder = null;
              if (ping_ != null) {
                subBuilder = ping_.toBuilder();
              }
              ping_ = input.readMessage(MessageModel.PingMessage.Ping.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(ping_);
                ping_ = subBuilder.buildPartial();
              }

              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              MessageModel.PingMessage.class, MessageModel.PingMessage.Builder.class);
    }

    public interface PingOrBuilder extends
        // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.PingMessage.Ping)
        com.google.protobuf.MessageOrBuilder {

      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
       */
      boolean hasToHost();
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
       */
      MessageModel.PingMessage.Ping.Address getToHost();
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
       */
      MessageModel.PingMessage.Ping.AddressOrBuilder getToHostOrBuilder();

      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
       */
      boolean hasFromHost();
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
       */
      MessageModel.PingMessage.Ping.Address getFromHost();
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
       */
      MessageModel.PingMessage.Ping.AddressOrBuilder getFromHostOrBuilder();

      /**
       * <code>uint64 expires = 3;</code>
       */
      long getExpires();

      /**
       * <code>int32 version = 4;</code>
       */
      int getVersion();
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.PingMessage.Ping}
     */
    public  static final class Ping extends
        com.google.protobuf.GeneratedMessageV3 implements
        // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.PingMessage.Ping)
        PingOrBuilder {
    private static final long serialVersionUID = 0L;
      // Use Ping.newBuilder() to construct.
      private Ping(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
        super(builder);
      }
      private Ping() {
      }

      @java.lang.Override
      public final com.google.protobuf.UnknownFieldSet
      getUnknownFields() {
        return this.unknownFields;
      }
      private Ping(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        this();
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        int mutable_bitField0_ = 0;
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
            com.google.protobuf.UnknownFieldSet.newBuilder();
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 10: {
                MessageModel.PingMessage.Ping.Address.Builder subBuilder = null;
                if (toHost_ != null) {
                  subBuilder = toHost_.toBuilder();
                }
                toHost_ = input.readMessage(MessageModel.PingMessage.Ping.Address.parser(), extensionRegistry);
                if (subBuilder != null) {
                  subBuilder.mergeFrom(toHost_);
                  toHost_ = subBuilder.buildPartial();
                }

                break;
              }
              case 18: {
                MessageModel.PingMessage.Ping.Address.Builder subBuilder = null;
                if (fromHost_ != null) {
                  subBuilder = fromHost_.toBuilder();
                }
                fromHost_ = input.readMessage(MessageModel.PingMessage.Ping.Address.parser(), extensionRegistry);
                if (subBuilder != null) {
                  subBuilder.mergeFrom(fromHost_);
                  fromHost_ = subBuilder.buildPartial();
                }

                break;
              }
              case 24: {

                expires_ = input.readUInt64();
                break;
              }
              case 32: {

                version_ = input.readInt32();
                break;
              }
              default: {
                if (!parseUnknownField(
                    input, unknownFields, extensionRegistry, tag)) {
                  done = true;
                }
                break;
              }
            }
          }
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(this);
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(
              e).setUnfinishedMessage(this);
        } finally {
          this.unknownFields = unknownFields.build();
          makeExtensionsImmutable();
        }
      }
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.PingMessage.Ping.class, MessageModel.PingMessage.Ping.Builder.class);
      }

      public interface AddressOrBuilder extends
          // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.PingMessage.Ping.Address)
          com.google.protobuf.MessageOrBuilder {

        /**
         * <code>string ip = 1;</code>
         */
        java.lang.String getIp();
        /**
         * <code>string ip = 1;</code>
         */
        com.google.protobuf.ByteString
            getIpBytes();

        /**
         * <code>int32 prot = 2;</code>
         */
        int getProt();
      }
      /**
       * Protobuf type {@code org.ethereum.protobuf.PingMessage.Ping.Address}
       */
      public  static final class Address extends
          com.google.protobuf.GeneratedMessageV3 implements
          // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.PingMessage.Ping.Address)
          AddressOrBuilder {
      private static final long serialVersionUID = 0L;
        // Use Address.newBuilder() to construct.
        private Address(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
          super(builder);
        }
        private Address() {
          ip_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
          return this.unknownFields;
        }
        private Address(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          this();
          if (extensionRegistry == null) {
            throw new java.lang.NullPointerException();
          }
          int mutable_bitField0_ = 0;
          com.google.protobuf.UnknownFieldSet.Builder unknownFields =
              com.google.protobuf.UnknownFieldSet.newBuilder();
          try {
            boolean done = false;
            while (!done) {
              int tag = input.readTag();
              switch (tag) {
                case 0:
                  done = true;
                  break;
                case 10: {
                  java.lang.String s = input.readStringRequireUtf8();

                  ip_ = s;
                  break;
                }
                case 16: {

                  prot_ = input.readInt32();
                  break;
                }
                default: {
                  if (!parseUnknownField(
                      input, unknownFields, extensionRegistry, tag)) {
                    done = true;
                  }
                  break;
                }
              }
            }
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(this);
          } catch (java.io.IOException e) {
            throw new com.google.protobuf.InvalidProtocolBufferException(
                e).setUnfinishedMessage(this);
          } finally {
            this.unknownFields = unknownFields.build();
            makeExtensionsImmutable();
          }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
          return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
          return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_fieldAccessorTable
              .ensureFieldAccessorsInitialized(
                  MessageModel.PingMessage.Ping.Address.class, MessageModel.PingMessage.Ping.Address.Builder.class);
        }

        public static final int IP_FIELD_NUMBER = 1;
        private volatile java.lang.Object ip_;
        /**
         * <code>string ip = 1;</code>
         */
        public java.lang.String getIp() {
          java.lang.Object ref = ip_;
          if (ref instanceof java.lang.String) {
            return (java.lang.String) ref;
          } else {
            com.google.protobuf.ByteString bs =
                (com.google.protobuf.ByteString) ref;
            java.lang.String s = bs.toStringUtf8();
            ip_ = s;
            return s;
          }
        }
        /**
         * <code>string ip = 1;</code>
         */
        public com.google.protobuf.ByteString
            getIpBytes() {
          java.lang.Object ref = ip_;
          if (ref instanceof java.lang.String) {
            com.google.protobuf.ByteString b =
                com.google.protobuf.ByteString.copyFromUtf8(
                    (java.lang.String) ref);
            ip_ = b;
            return b;
          } else {
            return (com.google.protobuf.ByteString) ref;
          }
        }

        public static final int PROT_FIELD_NUMBER = 2;
        private int prot_;
        /**
         * <code>int32 prot = 2;</code>
         */
        public int getProt() {
          return prot_;
        }

        private byte memoizedIsInitialized = -1;
        @java.lang.Override
        public final boolean isInitialized() {
          byte isInitialized = memoizedIsInitialized;
          if (isInitialized == 1) return true;
          if (isInitialized == 0) return false;

          memoizedIsInitialized = 1;
          return true;
        }

        @java.lang.Override
        public void writeTo(com.google.protobuf.CodedOutputStream output)
                            throws java.io.IOException {
          if (!getIpBytes().isEmpty()) {
            com.google.protobuf.GeneratedMessageV3.writeString(output, 1, ip_);
          }
          if (prot_ != 0) {
            output.writeInt32(2, prot_);
          }
          unknownFields.writeTo(output);
        }

        @java.lang.Override
        public int getSerializedSize() {
          int size = memoizedSize;
          if (size != -1) return size;

          size = 0;
          if (!getIpBytes().isEmpty()) {
            size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, ip_);
          }
          if (prot_ != 0) {
            size += com.google.protobuf.CodedOutputStream
              .computeInt32Size(2, prot_);
          }
          size += unknownFields.getSerializedSize();
          memoizedSize = size;
          return size;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
          if (obj == this) {
           return true;
          }
          if (!(obj instanceof MessageModel.PingMessage.Ping.Address)) {
            return super.equals(obj);
          }
          MessageModel.PingMessage.Ping.Address other = (MessageModel.PingMessage.Ping.Address) obj;

          if (!getIp()
              .equals(other.getIp())) return false;
          if (getProt()
              != other.getProt()) return false;
          if (!unknownFields.equals(other.unknownFields)) return false;
          return true;
        }

        @java.lang.Override
        public int hashCode() {
          if (memoizedHashCode != 0) {
            return memoizedHashCode;
          }
          int hash = 41;
          hash = (19 * hash) + getDescriptor().hashCode();
          hash = (37 * hash) + IP_FIELD_NUMBER;
          hash = (53 * hash) + getIp().hashCode();
          hash = (37 * hash) + PROT_FIELD_NUMBER;
          hash = (53 * hash) + getProt();
          hash = (29 * hash) + unknownFields.hashCode();
          memoizedHashCode = hash;
          return hash;
        }

        public static MessageModel.PingMessage.Ping.Address parseFrom(
            java.nio.ByteBuffer data)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data);
        }
        public static MessageModel.PingMessage.Ping.Address parseFrom(
            java.nio.ByteBuffer data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MessageModel.PingMessage.Ping.Address parseFrom(
            com.google.protobuf.ByteString data)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data);
        }
        public static MessageModel.PingMessage.Ping.Address parseFrom(
            com.google.protobuf.ByteString data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MessageModel.PingMessage.Ping.Address parseFrom(byte[] data)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data);
        }
        public static MessageModel.PingMessage.Ping.Address parseFrom(
            byte[] data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MessageModel.PingMessage.Ping.Address parseFrom(java.io.InputStream input)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseWithIOException(PARSER, input);
        }
        public static MessageModel.PingMessage.Ping.Address parseFrom(
            java.io.InputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MessageModel.PingMessage.Ping.Address parseDelimitedFrom(java.io.InputStream input)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseDelimitedWithIOException(PARSER, input);
        }
        public static MessageModel.PingMessage.Ping.Address parseDelimitedFrom(
            java.io.InputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MessageModel.PingMessage.Ping.Address parseFrom(
            com.google.protobuf.CodedInputStream input)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseWithIOException(PARSER, input);
        }
        public static MessageModel.PingMessage.Ping.Address parseFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseWithIOException(PARSER, input, extensionRegistry);
        }

        @java.lang.Override
        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
          return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MessageModel.PingMessage.Ping.Address prototype) {
          return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        @java.lang.Override
        public Builder toBuilder() {
          return this == DEFAULT_INSTANCE
              ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
          Builder builder = new Builder(parent);
          return builder;
        }
        /**
         * Protobuf type {@code org.ethereum.protobuf.PingMessage.Ping.Address}
         */
        public static final class Builder extends
            com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
            // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.PingMessage.Ping.Address)
            MessageModel.PingMessage.Ping.AddressOrBuilder {
          public static final com.google.protobuf.Descriptors.Descriptor
              getDescriptor() {
            return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_descriptor;
          }

          @java.lang.Override
          protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
              internalGetFieldAccessorTable() {
            return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_fieldAccessorTable
                .ensureFieldAccessorsInitialized(
                    MessageModel.PingMessage.Ping.Address.class, MessageModel.PingMessage.Ping.Address.Builder.class);
          }

          // Construct using MessageModel.PingMessage.Ping.Address.newBuilder()
          private Builder() {
            maybeForceBuilderInitialization();
          }

          private Builder(
              com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            maybeForceBuilderInitialization();
          }
          private void maybeForceBuilderInitialization() {
            if (com.google.protobuf.GeneratedMessageV3
                    .alwaysUseFieldBuilders) {
            }
          }
          @java.lang.Override
          public Builder clear() {
            super.clear();
            ip_ = "";

            prot_ = 0;

            return this;
          }

          @java.lang.Override
          public com.google.protobuf.Descriptors.Descriptor
              getDescriptorForType() {
            return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_descriptor;
          }

          @java.lang.Override
          public MessageModel.PingMessage.Ping.Address getDefaultInstanceForType() {
            return MessageModel.PingMessage.Ping.Address.getDefaultInstance();
          }

          @java.lang.Override
          public MessageModel.PingMessage.Ping.Address build() {
            MessageModel.PingMessage.Ping.Address result = buildPartial();
            if (!result.isInitialized()) {
              throw newUninitializedMessageException(result);
            }
            return result;
          }

          @java.lang.Override
          public MessageModel.PingMessage.Ping.Address buildPartial() {
            MessageModel.PingMessage.Ping.Address result = new MessageModel.PingMessage.Ping.Address(this);
            result.ip_ = ip_;
            result.prot_ = prot_;
            onBuilt();
            return result;
          }

          @java.lang.Override
          public Builder clone() {
            return super.clone();
          }
          @java.lang.Override
          public Builder setField(
              com.google.protobuf.Descriptors.FieldDescriptor field,
              java.lang.Object value) {
            return super.setField(field, value);
          }
          @java.lang.Override
          public Builder clearField(
              com.google.protobuf.Descriptors.FieldDescriptor field) {
            return super.clearField(field);
          }
          @java.lang.Override
          public Builder clearOneof(
              com.google.protobuf.Descriptors.OneofDescriptor oneof) {
            return super.clearOneof(oneof);
          }
          @java.lang.Override
          public Builder setRepeatedField(
              com.google.protobuf.Descriptors.FieldDescriptor field,
              int index, java.lang.Object value) {
            return super.setRepeatedField(field, index, value);
          }
          @java.lang.Override
          public Builder addRepeatedField(
              com.google.protobuf.Descriptors.FieldDescriptor field,
              java.lang.Object value) {
            return super.addRepeatedField(field, value);
          }
          @java.lang.Override
          public Builder mergeFrom(com.google.protobuf.Message other) {
            if (other instanceof MessageModel.PingMessage.Ping.Address) {
              return mergeFrom((MessageModel.PingMessage.Ping.Address)other);
            } else {
              super.mergeFrom(other);
              return this;
            }
          }

          public Builder mergeFrom(MessageModel.PingMessage.Ping.Address other) {
            if (other == MessageModel.PingMessage.Ping.Address.getDefaultInstance()) return this;
            if (!other.getIp().isEmpty()) {
              ip_ = other.ip_;
              onChanged();
            }
            if (other.getProt() != 0) {
              setProt(other.getProt());
            }
            this.mergeUnknownFields(other.unknownFields);
            onChanged();
            return this;
          }

          @java.lang.Override
          public final boolean isInitialized() {
            return true;
          }

          @java.lang.Override
          public Builder mergeFrom(
              com.google.protobuf.CodedInputStream input,
              com.google.protobuf.ExtensionRegistryLite extensionRegistry)
              throws java.io.IOException {
            MessageModel.PingMessage.Ping.Address parsedMessage = null;
            try {
              parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
              parsedMessage = (MessageModel.PingMessage.Ping.Address) e.getUnfinishedMessage();
              throw e.unwrapIOException();
            } finally {
              if (parsedMessage != null) {
                mergeFrom(parsedMessage);
              }
            }
            return this;
          }

          private java.lang.Object ip_ = "";
          /**
           * <code>string ip = 1;</code>
           */
          public java.lang.String getIp() {
            java.lang.Object ref = ip_;
            if (!(ref instanceof java.lang.String)) {
              com.google.protobuf.ByteString bs =
                  (com.google.protobuf.ByteString) ref;
              java.lang.String s = bs.toStringUtf8();
              ip_ = s;
              return s;
            } else {
              return (java.lang.String) ref;
            }
          }
          /**
           * <code>string ip = 1;</code>
           */
          public com.google.protobuf.ByteString
              getIpBytes() {
            java.lang.Object ref = ip_;
            if (ref instanceof String) {
              com.google.protobuf.ByteString b =
                  com.google.protobuf.ByteString.copyFromUtf8(
                      (java.lang.String) ref);
              ip_ = b;
              return b;
            } else {
              return (com.google.protobuf.ByteString) ref;
            }
          }
          /**
           * <code>string ip = 1;</code>
           */
          public Builder setIp(
              java.lang.String value) {
            if (value == null) {
    throw new NullPointerException();
  }

            ip_ = value;
            onChanged();
            return this;
          }
          /**
           * <code>string ip = 1;</code>
           */
          public Builder clearIp() {

            ip_ = getDefaultInstance().getIp();
            onChanged();
            return this;
          }
          /**
           * <code>string ip = 1;</code>
           */
          public Builder setIpBytes(
              com.google.protobuf.ByteString value) {
            if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

            ip_ = value;
            onChanged();
            return this;
          }

          private int prot_ ;
          /**
           * <code>int32 prot = 2;</code>
           */
          public int getProt() {
            return prot_;
          }
          /**
           * <code>int32 prot = 2;</code>
           */
          public Builder setProt(int value) {

            prot_ = value;
            onChanged();
            return this;
          }
          /**
           * <code>int32 prot = 2;</code>
           */
          public Builder clearProt() {

            prot_ = 0;
            onChanged();
            return this;
          }
          @java.lang.Override
          public final Builder setUnknownFields(
              final com.google.protobuf.UnknownFieldSet unknownFields) {
            return super.setUnknownFields(unknownFields);
          }

          @java.lang.Override
          public final Builder mergeUnknownFields(
              final com.google.protobuf.UnknownFieldSet unknownFields) {
            return super.mergeUnknownFields(unknownFields);
          }


          // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.PingMessage.Ping.Address)
        }

        // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.PingMessage.Ping.Address)
        private static final MessageModel.PingMessage.Ping.Address DEFAULT_INSTANCE;
        static {
          DEFAULT_INSTANCE = new MessageModel.PingMessage.Ping.Address();
        }

        public static MessageModel.PingMessage.Ping.Address getDefaultInstance() {
          return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<Address>
            PARSER = new com.google.protobuf.AbstractParser<Address>() {
          @java.lang.Override
          public Address parsePartialFrom(
              com.google.protobuf.CodedInputStream input,
              com.google.protobuf.ExtensionRegistryLite extensionRegistry)
              throws com.google.protobuf.InvalidProtocolBufferException {
            return new Address(input, extensionRegistry);
          }
        };

        public static com.google.protobuf.Parser<Address> parser() {
          return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<Address> getParserForType() {
          return PARSER;
        }

        @java.lang.Override
        public MessageModel.PingMessage.Ping.Address getDefaultInstanceForType() {
          return DEFAULT_INSTANCE;
        }

      }

      public static final int TOHOST_FIELD_NUMBER = 1;
      private MessageModel.PingMessage.Ping.Address toHost_;
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
       */
      public boolean hasToHost() {
        return toHost_ != null;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
       */
      public MessageModel.PingMessage.Ping.Address getToHost() {
        return toHost_ == null ? MessageModel.PingMessage.Ping.Address.getDefaultInstance() : toHost_;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
       */
      public MessageModel.PingMessage.Ping.AddressOrBuilder getToHostOrBuilder() {
        return getToHost();
      }

      public static final int FROMHOST_FIELD_NUMBER = 2;
      private MessageModel.PingMessage.Ping.Address fromHost_;
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
       */
      public boolean hasFromHost() {
        return fromHost_ != null;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
       */
      public MessageModel.PingMessage.Ping.Address getFromHost() {
        return fromHost_ == null ? MessageModel.PingMessage.Ping.Address.getDefaultInstance() : fromHost_;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
       */
      public MessageModel.PingMessage.Ping.AddressOrBuilder getFromHostOrBuilder() {
        return getFromHost();
      }

      public static final int EXPIRES_FIELD_NUMBER = 3;
      private long expires_;
      /**
       * <code>uint64 expires = 3;</code>
       */
      public long getExpires() {
        return expires_;
      }

      public static final int VERSION_FIELD_NUMBER = 4;
      private int version_;
      /**
       * <code>int32 version = 4;</code>
       */
      public int getVersion() {
        return version_;
      }

      private byte memoizedIsInitialized = -1;
      @java.lang.Override
      public final boolean isInitialized() {
        byte isInitialized = memoizedIsInitialized;
        if (isInitialized == 1) return true;
        if (isInitialized == 0) return false;

        memoizedIsInitialized = 1;
        return true;
      }

      @java.lang.Override
      public void writeTo(com.google.protobuf.CodedOutputStream output)
                          throws java.io.IOException {
        if (toHost_ != null) {
          output.writeMessage(1, getToHost());
        }
        if (fromHost_ != null) {
          output.writeMessage(2, getFromHost());
        }
        if (expires_ != 0L) {
          output.writeUInt64(3, expires_);
        }
        if (version_ != 0) {
          output.writeInt32(4, version_);
        }
        unknownFields.writeTo(output);
      }

      @java.lang.Override
      public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1) return size;

        size = 0;
        if (toHost_ != null) {
          size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(1, getToHost());
        }
        if (fromHost_ != null) {
          size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(2, getFromHost());
        }
        if (expires_ != 0L) {
          size += com.google.protobuf.CodedOutputStream
            .computeUInt64Size(3, expires_);
        }
        if (version_ != 0) {
          size += com.google.protobuf.CodedOutputStream
            .computeInt32Size(4, version_);
        }
        size += unknownFields.getSerializedSize();
        memoizedSize = size;
        return size;
      }

      @java.lang.Override
      public boolean equals(final java.lang.Object obj) {
        if (obj == this) {
         return true;
        }
        if (!(obj instanceof MessageModel.PingMessage.Ping)) {
          return super.equals(obj);
        }
        MessageModel.PingMessage.Ping other = (MessageModel.PingMessage.Ping) obj;

        if (hasToHost() != other.hasToHost()) return false;
        if (hasToHost()) {
          if (!getToHost()
              .equals(other.getToHost())) return false;
        }
        if (hasFromHost() != other.hasFromHost()) return false;
        if (hasFromHost()) {
          if (!getFromHost()
              .equals(other.getFromHost())) return false;
        }
        if (getExpires()
            != other.getExpires()) return false;
        if (getVersion()
            != other.getVersion()) return false;
        if (!unknownFields.equals(other.unknownFields)) return false;
        return true;
      }

      @java.lang.Override
      public int hashCode() {
        if (memoizedHashCode != 0) {
          return memoizedHashCode;
        }
        int hash = 41;
        hash = (19 * hash) + getDescriptor().hashCode();
        if (hasToHost()) {
          hash = (37 * hash) + TOHOST_FIELD_NUMBER;
          hash = (53 * hash) + getToHost().hashCode();
        }
        if (hasFromHost()) {
          hash = (37 * hash) + FROMHOST_FIELD_NUMBER;
          hash = (53 * hash) + getFromHost().hashCode();
        }
        hash = (37 * hash) + EXPIRES_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getExpires());
        hash = (37 * hash) + VERSION_FIELD_NUMBER;
        hash = (53 * hash) + getVersion();
        hash = (29 * hash) + unknownFields.hashCode();
        memoizedHashCode = hash;
        return hash;
      }

      public static MessageModel.PingMessage.Ping parseFrom(
          java.nio.ByteBuffer data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.PingMessage.Ping parseFrom(
          java.nio.ByteBuffer data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.PingMessage.Ping parseFrom(
          com.google.protobuf.ByteString data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.PingMessage.Ping parseFrom(
          com.google.protobuf.ByteString data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.PingMessage.Ping parseFrom(byte[] data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.PingMessage.Ping parseFrom(
          byte[] data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.PingMessage.Ping parseFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.PingMessage.Ping parseFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.PingMessage.Ping parseDelimitedFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input);
      }
      public static MessageModel.PingMessage.Ping parseDelimitedFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.PingMessage.Ping parseFrom(
          com.google.protobuf.CodedInputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.PingMessage.Ping parseFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }

      @java.lang.Override
      public Builder newBuilderForType() { return newBuilder(); }
      public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
      }
      public static Builder newBuilder(MessageModel.PingMessage.Ping prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }
      @java.lang.Override
      public Builder toBuilder() {
        return this == DEFAULT_INSTANCE
            ? new Builder() : new Builder().mergeFrom(this);
      }

      @java.lang.Override
      protected Builder newBuilderForType(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
      }
      /**
       * Protobuf type {@code org.ethereum.protobuf.PingMessage.Ping}
       */
      public static final class Builder extends
          com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
          // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.PingMessage.Ping)
          MessageModel.PingMessage.PingOrBuilder {
        public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
          return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
          return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_fieldAccessorTable
              .ensureFieldAccessorsInitialized(
                  MessageModel.PingMessage.Ping.class, MessageModel.PingMessage.Ping.Builder.class);
        }

        // Construct using MessageModel.PingMessage.Ping.newBuilder()
        private Builder() {
          maybeForceBuilderInitialization();
        }

        private Builder(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
          super(parent);
          maybeForceBuilderInitialization();
        }
        private void maybeForceBuilderInitialization() {
          if (com.google.protobuf.GeneratedMessageV3
                  .alwaysUseFieldBuilders) {
          }
        }
        @java.lang.Override
        public Builder clear() {
          super.clear();
          if (toHostBuilder_ == null) {
            toHost_ = null;
          } else {
            toHost_ = null;
            toHostBuilder_ = null;
          }
          if (fromHostBuilder_ == null) {
            fromHost_ = null;
          } else {
            fromHost_ = null;
            fromHostBuilder_ = null;
          }
          expires_ = 0L;

          version_ = 0;

          return this;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
          return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_Ping_descriptor;
        }

        @java.lang.Override
        public MessageModel.PingMessage.Ping getDefaultInstanceForType() {
          return MessageModel.PingMessage.Ping.getDefaultInstance();
        }

        @java.lang.Override
        public MessageModel.PingMessage.Ping build() {
          MessageModel.PingMessage.Ping result = buildPartial();
          if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
          }
          return result;
        }

        @java.lang.Override
        public MessageModel.PingMessage.Ping buildPartial() {
          MessageModel.PingMessage.Ping result = new MessageModel.PingMessage.Ping(this);
          if (toHostBuilder_ == null) {
            result.toHost_ = toHost_;
          } else {
            result.toHost_ = toHostBuilder_.build();
          }
          if (fromHostBuilder_ == null) {
            result.fromHost_ = fromHost_;
          } else {
            result.fromHost_ = fromHostBuilder_.build();
          }
          result.expires_ = expires_;
          result.version_ = version_;
          onBuilt();
          return result;
        }

        @java.lang.Override
        public Builder clone() {
          return super.clone();
        }
        @java.lang.Override
        public Builder setField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.setField(field, value);
        }
        @java.lang.Override
        public Builder clearField(
            com.google.protobuf.Descriptors.FieldDescriptor field) {
          return super.clearField(field);
        }
        @java.lang.Override
        public Builder clearOneof(
            com.google.protobuf.Descriptors.OneofDescriptor oneof) {
          return super.clearOneof(oneof);
        }
        @java.lang.Override
        public Builder setRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            int index, java.lang.Object value) {
          return super.setRepeatedField(field, index, value);
        }
        @java.lang.Override
        public Builder addRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.addRepeatedField(field, value);
        }
        @java.lang.Override
        public Builder mergeFrom(com.google.protobuf.Message other) {
          if (other instanceof MessageModel.PingMessage.Ping) {
            return mergeFrom((MessageModel.PingMessage.Ping)other);
          } else {
            super.mergeFrom(other);
            return this;
          }
        }

        public Builder mergeFrom(MessageModel.PingMessage.Ping other) {
          if (other == MessageModel.PingMessage.Ping.getDefaultInstance()) return this;
          if (other.hasToHost()) {
            mergeToHost(other.getToHost());
          }
          if (other.hasFromHost()) {
            mergeFromHost(other.getFromHost());
          }
          if (other.getExpires() != 0L) {
            setExpires(other.getExpires());
          }
          if (other.getVersion() != 0) {
            setVersion(other.getVersion());
          }
          this.mergeUnknownFields(other.unknownFields);
          onChanged();
          return this;
        }

        @java.lang.Override
        public final boolean isInitialized() {
          return true;
        }

        @java.lang.Override
        public Builder mergeFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          MessageModel.PingMessage.Ping parsedMessage = null;
          try {
            parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            parsedMessage = (MessageModel.PingMessage.Ping) e.getUnfinishedMessage();
            throw e.unwrapIOException();
          } finally {
            if (parsedMessage != null) {
              mergeFrom(parsedMessage);
            }
          }
          return this;
        }

        private MessageModel.PingMessage.Ping.Address toHost_;
        private com.google.protobuf.SingleFieldBuilderV3<
            MessageModel.PingMessage.Ping.Address, MessageModel.PingMessage.Ping.Address.Builder, MessageModel.PingMessage.Ping.AddressOrBuilder> toHostBuilder_;
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
         */
        public boolean hasToHost() {
          return toHostBuilder_ != null || toHost_ != null;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
         */
        public MessageModel.PingMessage.Ping.Address getToHost() {
          if (toHostBuilder_ == null) {
            return toHost_ == null ? MessageModel.PingMessage.Ping.Address.getDefaultInstance() : toHost_;
          } else {
            return toHostBuilder_.getMessage();
          }
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
         */
        public Builder setToHost(MessageModel.PingMessage.Ping.Address value) {
          if (toHostBuilder_ == null) {
            if (value == null) {
              throw new NullPointerException();
            }
            toHost_ = value;
            onChanged();
          } else {
            toHostBuilder_.setMessage(value);
          }

          return this;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
         */
        public Builder setToHost(
            MessageModel.PingMessage.Ping.Address.Builder builderForValue) {
          if (toHostBuilder_ == null) {
            toHost_ = builderForValue.build();
            onChanged();
          } else {
            toHostBuilder_.setMessage(builderForValue.build());
          }

          return this;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
         */
        public Builder mergeToHost(MessageModel.PingMessage.Ping.Address value) {
          if (toHostBuilder_ == null) {
            if (toHost_ != null) {
              toHost_ =
                MessageModel.PingMessage.Ping.Address.newBuilder(toHost_).mergeFrom(value).buildPartial();
            } else {
              toHost_ = value;
            }
            onChanged();
          } else {
            toHostBuilder_.mergeFrom(value);
          }

          return this;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
         */
        public Builder clearToHost() {
          if (toHostBuilder_ == null) {
            toHost_ = null;
            onChanged();
          } else {
            toHost_ = null;
            toHostBuilder_ = null;
          }

          return this;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
         */
        public MessageModel.PingMessage.Ping.Address.Builder getToHostBuilder() {

          onChanged();
          return getToHostFieldBuilder().getBuilder();
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
         */
        public MessageModel.PingMessage.Ping.AddressOrBuilder getToHostOrBuilder() {
          if (toHostBuilder_ != null) {
            return toHostBuilder_.getMessageOrBuilder();
          } else {
            return toHost_ == null ?
                MessageModel.PingMessage.Ping.Address.getDefaultInstance() : toHost_;
          }
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address toHost = 1;</code>
         */
        private com.google.protobuf.SingleFieldBuilderV3<
            MessageModel.PingMessage.Ping.Address, MessageModel.PingMessage.Ping.Address.Builder, MessageModel.PingMessage.Ping.AddressOrBuilder>
            getToHostFieldBuilder() {
          if (toHostBuilder_ == null) {
            toHostBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                MessageModel.PingMessage.Ping.Address, MessageModel.PingMessage.Ping.Address.Builder, MessageModel.PingMessage.Ping.AddressOrBuilder>(
                    getToHost(),
                    getParentForChildren(),
                    isClean());
            toHost_ = null;
          }
          return toHostBuilder_;
        }

        private MessageModel.PingMessage.Ping.Address fromHost_;
        private com.google.protobuf.SingleFieldBuilderV3<
            MessageModel.PingMessage.Ping.Address, MessageModel.PingMessage.Ping.Address.Builder, MessageModel.PingMessage.Ping.AddressOrBuilder> fromHostBuilder_;
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
         */
        public boolean hasFromHost() {
          return fromHostBuilder_ != null || fromHost_ != null;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
         */
        public MessageModel.PingMessage.Ping.Address getFromHost() {
          if (fromHostBuilder_ == null) {
            return fromHost_ == null ? MessageModel.PingMessage.Ping.Address.getDefaultInstance() : fromHost_;
          } else {
            return fromHostBuilder_.getMessage();
          }
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
         */
        public Builder setFromHost(MessageModel.PingMessage.Ping.Address value) {
          if (fromHostBuilder_ == null) {
            if (value == null) {
              throw new NullPointerException();
            }
            fromHost_ = value;
            onChanged();
          } else {
            fromHostBuilder_.setMessage(value);
          }

          return this;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
         */
        public Builder setFromHost(
            MessageModel.PingMessage.Ping.Address.Builder builderForValue) {
          if (fromHostBuilder_ == null) {
            fromHost_ = builderForValue.build();
            onChanged();
          } else {
            fromHostBuilder_.setMessage(builderForValue.build());
          }

          return this;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
         */
        public Builder mergeFromHost(MessageModel.PingMessage.Ping.Address value) {
          if (fromHostBuilder_ == null) {
            if (fromHost_ != null) {
              fromHost_ =
                MessageModel.PingMessage.Ping.Address.newBuilder(fromHost_).mergeFrom(value).buildPartial();
            } else {
              fromHost_ = value;
            }
            onChanged();
          } else {
            fromHostBuilder_.mergeFrom(value);
          }

          return this;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
         */
        public Builder clearFromHost() {
          if (fromHostBuilder_ == null) {
            fromHost_ = null;
            onChanged();
          } else {
            fromHost_ = null;
            fromHostBuilder_ = null;
          }

          return this;
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
         */
        public MessageModel.PingMessage.Ping.Address.Builder getFromHostBuilder() {

          onChanged();
          return getFromHostFieldBuilder().getBuilder();
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
         */
        public MessageModel.PingMessage.Ping.AddressOrBuilder getFromHostOrBuilder() {
          if (fromHostBuilder_ != null) {
            return fromHostBuilder_.getMessageOrBuilder();
          } else {
            return fromHost_ == null ?
                MessageModel.PingMessage.Ping.Address.getDefaultInstance() : fromHost_;
          }
        }
        /**
         * <code>.org.ethereum.protobuf.PingMessage.Ping.Address fromHost = 2;</code>
         */
        private com.google.protobuf.SingleFieldBuilderV3<
            MessageModel.PingMessage.Ping.Address, MessageModel.PingMessage.Ping.Address.Builder, MessageModel.PingMessage.Ping.AddressOrBuilder>
            getFromHostFieldBuilder() {
          if (fromHostBuilder_ == null) {
            fromHostBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                MessageModel.PingMessage.Ping.Address, MessageModel.PingMessage.Ping.Address.Builder, MessageModel.PingMessage.Ping.AddressOrBuilder>(
                    getFromHost(),
                    getParentForChildren(),
                    isClean());
            fromHost_ = null;
          }
          return fromHostBuilder_;
        }

        private long expires_ ;
        /**
         * <code>uint64 expires = 3;</code>
         */
        public long getExpires() {
          return expires_;
        }
        /**
         * <code>uint64 expires = 3;</code>
         */
        public Builder setExpires(long value) {

          expires_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>uint64 expires = 3;</code>
         */
        public Builder clearExpires() {

          expires_ = 0L;
          onChanged();
          return this;
        }

        private int version_ ;
        /**
         * <code>int32 version = 4;</code>
         */
        public int getVersion() {
          return version_;
        }
        /**
         * <code>int32 version = 4;</code>
         */
        public Builder setVersion(int value) {

          version_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>int32 version = 4;</code>
         */
        public Builder clearVersion() {

          version_ = 0;
          onChanged();
          return this;
        }
        @java.lang.Override
        public final Builder setUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.setUnknownFields(unknownFields);
        }

        @java.lang.Override
        public final Builder mergeUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.mergeUnknownFields(unknownFields);
        }


        // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.PingMessage.Ping)
      }

      // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.PingMessage.Ping)
      private static final MessageModel.PingMessage.Ping DEFAULT_INSTANCE;
      static {
        DEFAULT_INSTANCE = new MessageModel.PingMessage.Ping();
      }

      public static MessageModel.PingMessage.Ping getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }

      private static final com.google.protobuf.Parser<Ping>
          PARSER = new com.google.protobuf.AbstractParser<Ping>() {
        @java.lang.Override
        public Ping parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new Ping(input, extensionRegistry);
        }
      };

      public static com.google.protobuf.Parser<Ping> parser() {
        return PARSER;
      }

      @java.lang.Override
      public com.google.protobuf.Parser<Ping> getParserForType() {
        return PARSER;
      }

      @java.lang.Override
      public MessageModel.PingMessage.Ping getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }

    }

    public static final int MAGIC_FIELD_NUMBER = 1;
    private volatile java.lang.Object magic_;
    /**
     * <code>string magic = 1;</code>
     */
    public java.lang.String getMagic() {
      java.lang.Object ref = magic_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        magic_ = s;
        return s;
      }
    }
    /**
     * <code>string magic = 1;</code>
     */
    public com.google.protobuf.ByteString
        getMagicBytes() {
      java.lang.Object ref = magic_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        magic_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int LENGTH_FIELD_NUMBER = 2;
    private int length_;
    /**
     * <code>sfixed32 length = 2;</code>
     */
    public int getLength() {
      return length_;
    }

    public static final int CHECKSUM_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString checksum_;
    /**
     * <code>bytes checksum = 3;</code>
     */
    public com.google.protobuf.ByteString getChecksum() {
      return checksum_;
    }

    public static final int PING_FIELD_NUMBER = 4;
    private MessageModel.PingMessage.Ping ping_;
    /**
     * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
     */
    public boolean hasPing() {
      return ping_ != null;
    }
    /**
     * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
     */
    public MessageModel.PingMessage.Ping getPing() {
      return ping_ == null ? MessageModel.PingMessage.Ping.getDefaultInstance() : ping_;
    }
    /**
     * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
     */
    public MessageModel.PingMessage.PingOrBuilder getPingOrBuilder() {
      return getPing();
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getMagicBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, magic_);
      }
      if (length_ != 0) {
        output.writeSFixed32(2, length_);
      }
      if (!checksum_.isEmpty()) {
        output.writeBytes(3, checksum_);
      }
      if (ping_ != null) {
        output.writeMessage(4, getPing());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getMagicBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, magic_);
      }
      if (length_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeSFixed32Size(2, length_);
      }
      if (!checksum_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, checksum_);
      }
      if (ping_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, getPing());
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof MessageModel.PingMessage)) {
        return super.equals(obj);
      }
      MessageModel.PingMessage other = (MessageModel.PingMessage) obj;

      if (!getMagic()
          .equals(other.getMagic())) return false;
      if (getLength()
          != other.getLength()) return false;
      if (!getChecksum()
          .equals(other.getChecksum())) return false;
      if (hasPing() != other.hasPing()) return false;
      if (hasPing()) {
        if (!getPing()
            .equals(other.getPing())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + MAGIC_FIELD_NUMBER;
      hash = (53 * hash) + getMagic().hashCode();
      hash = (37 * hash) + LENGTH_FIELD_NUMBER;
      hash = (53 * hash) + getLength();
      hash = (37 * hash) + CHECKSUM_FIELD_NUMBER;
      hash = (53 * hash) + getChecksum().hashCode();
      if (hasPing()) {
        hash = (37 * hash) + PING_FIELD_NUMBER;
        hash = (53 * hash) + getPing().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static MessageModel.PingMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.PingMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.PingMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.PingMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.PingMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.PingMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.PingMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.PingMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.PingMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static MessageModel.PingMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.PingMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.PingMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(MessageModel.PingMessage prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.PingMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.PingMessage)
        MessageModel.PingMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.PingMessage.class, MessageModel.PingMessage.Builder.class);
      }

      // Construct using MessageModel.PingMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        magic_ = "";

        length_ = 0;

        checksum_ = com.google.protobuf.ByteString.EMPTY;

        if (pingBuilder_ == null) {
          ping_ = null;
        } else {
          ping_ = null;
          pingBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return MessageModel.internal_static_org_ethereum_protobuf_PingMessage_descriptor;
      }

      @java.lang.Override
      public MessageModel.PingMessage getDefaultInstanceForType() {
        return MessageModel.PingMessage.getDefaultInstance();
      }

      @java.lang.Override
      public MessageModel.PingMessage build() {
        MessageModel.PingMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public MessageModel.PingMessage buildPartial() {
        MessageModel.PingMessage result = new MessageModel.PingMessage(this);
        result.magic_ = magic_;
        result.length_ = length_;
        result.checksum_ = checksum_;
        if (pingBuilder_ == null) {
          result.ping_ = ping_;
        } else {
          result.ping_ = pingBuilder_.build();
        }
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof MessageModel.PingMessage) {
          return mergeFrom((MessageModel.PingMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(MessageModel.PingMessage other) {
        if (other == MessageModel.PingMessage.getDefaultInstance()) return this;
        if (!other.getMagic().isEmpty()) {
          magic_ = other.magic_;
          onChanged();
        }
        if (other.getLength() != 0) {
          setLength(other.getLength());
        }
        if (other.getChecksum() != com.google.protobuf.ByteString.EMPTY) {
          setChecksum(other.getChecksum());
        }
        if (other.hasPing()) {
          mergePing(other.getPing());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        MessageModel.PingMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (MessageModel.PingMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object magic_ = "";
      /**
       * <code>string magic = 1;</code>
       */
      public java.lang.String getMagic() {
        java.lang.Object ref = magic_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          magic_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string magic = 1;</code>
       */
      public com.google.protobuf.ByteString
          getMagicBytes() {
        java.lang.Object ref = magic_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          magic_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder setMagic(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }

        magic_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder clearMagic() {

        magic_ = getDefaultInstance().getMagic();
        onChanged();
        return this;
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder setMagicBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

        magic_ = value;
        onChanged();
        return this;
      }

      private int length_ ;
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public int getLength() {
        return length_;
      }
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public Builder setLength(int value) {

        length_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public Builder clearLength() {

        length_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString checksum_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes checksum = 3;</code>
       */
      public com.google.protobuf.ByteString getChecksum() {
        return checksum_;
      }
      /**
       * <code>bytes checksum = 3;</code>
       */
      public Builder setChecksum(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        checksum_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes checksum = 3;</code>
       */
      public Builder clearChecksum() {

        checksum_ = getDefaultInstance().getChecksum();
        onChanged();
        return this;
      }

      private MessageModel.PingMessage.Ping ping_;
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.PingMessage.Ping, MessageModel.PingMessage.Ping.Builder, MessageModel.PingMessage.PingOrBuilder> pingBuilder_;
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
       */
      public boolean hasPing() {
        return pingBuilder_ != null || ping_ != null;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
       */
      public MessageModel.PingMessage.Ping getPing() {
        if (pingBuilder_ == null) {
          return ping_ == null ? MessageModel.PingMessage.Ping.getDefaultInstance() : ping_;
        } else {
          return pingBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
       */
      public Builder setPing(MessageModel.PingMessage.Ping value) {
        if (pingBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ping_ = value;
          onChanged();
        } else {
          pingBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
       */
      public Builder setPing(
          MessageModel.PingMessage.Ping.Builder builderForValue) {
        if (pingBuilder_ == null) {
          ping_ = builderForValue.build();
          onChanged();
        } else {
          pingBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
       */
      public Builder mergePing(MessageModel.PingMessage.Ping value) {
        if (pingBuilder_ == null) {
          if (ping_ != null) {
            ping_ =
              MessageModel.PingMessage.Ping.newBuilder(ping_).mergeFrom(value).buildPartial();
          } else {
            ping_ = value;
          }
          onChanged();
        } else {
          pingBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
       */
      public Builder clearPing() {
        if (pingBuilder_ == null) {
          ping_ = null;
          onChanged();
        } else {
          ping_ = null;
          pingBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
       */
      public MessageModel.PingMessage.Ping.Builder getPingBuilder() {

        onChanged();
        return getPingFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
       */
      public MessageModel.PingMessage.PingOrBuilder getPingOrBuilder() {
        if (pingBuilder_ != null) {
          return pingBuilder_.getMessageOrBuilder();
        } else {
          return ping_ == null ?
              MessageModel.PingMessage.Ping.getDefaultInstance() : ping_;
        }
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage.Ping ping = 4;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.PingMessage.Ping, MessageModel.PingMessage.Ping.Builder, MessageModel.PingMessage.PingOrBuilder>
          getPingFieldBuilder() {
        if (pingBuilder_ == null) {
          pingBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              MessageModel.PingMessage.Ping, MessageModel.PingMessage.Ping.Builder, MessageModel.PingMessage.PingOrBuilder>(
                  getPing(),
                  getParentForChildren(),
                  isClean());
          ping_ = null;
        }
        return pingBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.PingMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.PingMessage)
    private static final MessageModel.PingMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new MessageModel.PingMessage();
    }

    public static MessageModel.PingMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<PingMessage>
        PARSER = new com.google.protobuf.AbstractParser<PingMessage>() {
      @java.lang.Override
      public PingMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new PingMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<PingMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<PingMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public MessageModel.PingMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface FindNodeMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.FindNodeMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string magic = 1;</code>
     */
    java.lang.String getMagic();
    /**
     * <code>string magic = 1;</code>
     */
    com.google.protobuf.ByteString
        getMagicBytes();

    /**
     * <code>sfixed32 length = 2;</code>
     */
    int getLength();

    /**
     * <code>bytes checksum = 3;</code>
     */
    com.google.protobuf.ByteString getChecksum();

    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
     */
    boolean hasFindNode();
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
     */
    MessageModel.FindNodeMessage.FindNode getFindNode();
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
     */
    MessageModel.FindNodeMessage.FindNodeOrBuilder getFindNodeOrBuilder();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.FindNodeMessage}
   */
  public  static final class FindNodeMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.FindNodeMessage)
      FindNodeMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use FindNodeMessage.newBuilder() to construct.
    private FindNodeMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private FindNodeMessage() {
      magic_ = "";
      checksum_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private FindNodeMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              magic_ = s;
              break;
            }
            case 21: {

              length_ = input.readSFixed32();
              break;
            }
            case 26: {

              checksum_ = input.readBytes();
              break;
            }
            case 34: {
              MessageModel.FindNodeMessage.FindNode.Builder subBuilder = null;
              if (findNode_ != null) {
                subBuilder = findNode_.toBuilder();
              }
              findNode_ = input.readMessage(MessageModel.FindNodeMessage.FindNode.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(findNode_);
                findNode_ = subBuilder.buildPartial();
              }

              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              MessageModel.FindNodeMessage.class, MessageModel.FindNodeMessage.Builder.class);
    }

    public interface FindNodeOrBuilder extends
        // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.FindNodeMessage.FindNode)
        com.google.protobuf.MessageOrBuilder {

      /**
       * <code>bytes target = 1;</code>
       */
      com.google.protobuf.ByteString getTarget();

      /**
       * <code>uint64 expires = 2;</code>
       */
      long getExpires();
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.FindNodeMessage.FindNode}
     */
    public  static final class FindNode extends
        com.google.protobuf.GeneratedMessageV3 implements
        // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.FindNodeMessage.FindNode)
        FindNodeOrBuilder {
    private static final long serialVersionUID = 0L;
      // Use FindNode.newBuilder() to construct.
      private FindNode(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
        super(builder);
      }
      private FindNode() {
        target_ = com.google.protobuf.ByteString.EMPTY;
      }

      @java.lang.Override
      public final com.google.protobuf.UnknownFieldSet
      getUnknownFields() {
        return this.unknownFields;
      }
      private FindNode(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        this();
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        int mutable_bitField0_ = 0;
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
            com.google.protobuf.UnknownFieldSet.newBuilder();
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 10: {

                target_ = input.readBytes();
                break;
              }
              case 16: {

                expires_ = input.readUInt64();
                break;
              }
              default: {
                if (!parseUnknownField(
                    input, unknownFields, extensionRegistry, tag)) {
                  done = true;
                }
                break;
              }
            }
          }
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(this);
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(
              e).setUnfinishedMessage(this);
        } finally {
          this.unknownFields = unknownFields.build();
          makeExtensionsImmutable();
        }
      }
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.FindNodeMessage.FindNode.class, MessageModel.FindNodeMessage.FindNode.Builder.class);
      }

      public static final int TARGET_FIELD_NUMBER = 1;
      private com.google.protobuf.ByteString target_;
      /**
       * <code>bytes target = 1;</code>
       */
      public com.google.protobuf.ByteString getTarget() {
        return target_;
      }

      public static final int EXPIRES_FIELD_NUMBER = 2;
      private long expires_;
      /**
       * <code>uint64 expires = 2;</code>
       */
      public long getExpires() {
        return expires_;
      }

      private byte memoizedIsInitialized = -1;
      @java.lang.Override
      public final boolean isInitialized() {
        byte isInitialized = memoizedIsInitialized;
        if (isInitialized == 1) return true;
        if (isInitialized == 0) return false;

        memoizedIsInitialized = 1;
        return true;
      }

      @java.lang.Override
      public void writeTo(com.google.protobuf.CodedOutputStream output)
                          throws java.io.IOException {
        if (!target_.isEmpty()) {
          output.writeBytes(1, target_);
        }
        if (expires_ != 0L) {
          output.writeUInt64(2, expires_);
        }
        unknownFields.writeTo(output);
      }

      @java.lang.Override
      public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1) return size;

        size = 0;
        if (!target_.isEmpty()) {
          size += com.google.protobuf.CodedOutputStream
            .computeBytesSize(1, target_);
        }
        if (expires_ != 0L) {
          size += com.google.protobuf.CodedOutputStream
            .computeUInt64Size(2, expires_);
        }
        size += unknownFields.getSerializedSize();
        memoizedSize = size;
        return size;
      }

      @java.lang.Override
      public boolean equals(final java.lang.Object obj) {
        if (obj == this) {
         return true;
        }
        if (!(obj instanceof MessageModel.FindNodeMessage.FindNode)) {
          return super.equals(obj);
        }
        MessageModel.FindNodeMessage.FindNode other = (MessageModel.FindNodeMessage.FindNode) obj;

        if (!getTarget()
            .equals(other.getTarget())) return false;
        if (getExpires()
            != other.getExpires()) return false;
        if (!unknownFields.equals(other.unknownFields)) return false;
        return true;
      }

      @java.lang.Override
      public int hashCode() {
        if (memoizedHashCode != 0) {
          return memoizedHashCode;
        }
        int hash = 41;
        hash = (19 * hash) + getDescriptor().hashCode();
        hash = (37 * hash) + TARGET_FIELD_NUMBER;
        hash = (53 * hash) + getTarget().hashCode();
        hash = (37 * hash) + EXPIRES_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getExpires());
        hash = (29 * hash) + unknownFields.hashCode();
        memoizedHashCode = hash;
        return hash;
      }

      public static MessageModel.FindNodeMessage.FindNode parseFrom(
          java.nio.ByteBuffer data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.FindNodeMessage.FindNode parseFrom(
          java.nio.ByteBuffer data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.FindNodeMessage.FindNode parseFrom(
          com.google.protobuf.ByteString data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.FindNodeMessage.FindNode parseFrom(
          com.google.protobuf.ByteString data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.FindNodeMessage.FindNode parseFrom(byte[] data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.FindNodeMessage.FindNode parseFrom(
          byte[] data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.FindNodeMessage.FindNode parseFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.FindNodeMessage.FindNode parseFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.FindNodeMessage.FindNode parseDelimitedFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input);
      }
      public static MessageModel.FindNodeMessage.FindNode parseDelimitedFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.FindNodeMessage.FindNode parseFrom(
          com.google.protobuf.CodedInputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.FindNodeMessage.FindNode parseFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }

      @java.lang.Override
      public Builder newBuilderForType() { return newBuilder(); }
      public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
      }
      public static Builder newBuilder(MessageModel.FindNodeMessage.FindNode prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }
      @java.lang.Override
      public Builder toBuilder() {
        return this == DEFAULT_INSTANCE
            ? new Builder() : new Builder().mergeFrom(this);
      }

      @java.lang.Override
      protected Builder newBuilderForType(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
      }
      /**
       * Protobuf type {@code org.ethereum.protobuf.FindNodeMessage.FindNode}
       */
      public static final class Builder extends
          com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
          // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.FindNodeMessage.FindNode)
          MessageModel.FindNodeMessage.FindNodeOrBuilder {
        public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
          return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
          return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_fieldAccessorTable
              .ensureFieldAccessorsInitialized(
                  MessageModel.FindNodeMessage.FindNode.class, MessageModel.FindNodeMessage.FindNode.Builder.class);
        }

        // Construct using MessageModel.FindNodeMessage.FindNode.newBuilder()
        private Builder() {
          maybeForceBuilderInitialization();
        }

        private Builder(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
          super(parent);
          maybeForceBuilderInitialization();
        }
        private void maybeForceBuilderInitialization() {
          if (com.google.protobuf.GeneratedMessageV3
                  .alwaysUseFieldBuilders) {
          }
        }
        @java.lang.Override
        public Builder clear() {
          super.clear();
          target_ = com.google.protobuf.ByteString.EMPTY;

          expires_ = 0L;

          return this;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
          return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_descriptor;
        }

        @java.lang.Override
        public MessageModel.FindNodeMessage.FindNode getDefaultInstanceForType() {
          return MessageModel.FindNodeMessage.FindNode.getDefaultInstance();
        }

        @java.lang.Override
        public MessageModel.FindNodeMessage.FindNode build() {
          MessageModel.FindNodeMessage.FindNode result = buildPartial();
          if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
          }
          return result;
        }

        @java.lang.Override
        public MessageModel.FindNodeMessage.FindNode buildPartial() {
          MessageModel.FindNodeMessage.FindNode result = new MessageModel.FindNodeMessage.FindNode(this);
          result.target_ = target_;
          result.expires_ = expires_;
          onBuilt();
          return result;
        }

        @java.lang.Override
        public Builder clone() {
          return super.clone();
        }
        @java.lang.Override
        public Builder setField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.setField(field, value);
        }
        @java.lang.Override
        public Builder clearField(
            com.google.protobuf.Descriptors.FieldDescriptor field) {
          return super.clearField(field);
        }
        @java.lang.Override
        public Builder clearOneof(
            com.google.protobuf.Descriptors.OneofDescriptor oneof) {
          return super.clearOneof(oneof);
        }
        @java.lang.Override
        public Builder setRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            int index, java.lang.Object value) {
          return super.setRepeatedField(field, index, value);
        }
        @java.lang.Override
        public Builder addRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.addRepeatedField(field, value);
        }
        @java.lang.Override
        public Builder mergeFrom(com.google.protobuf.Message other) {
          if (other instanceof MessageModel.FindNodeMessage.FindNode) {
            return mergeFrom((MessageModel.FindNodeMessage.FindNode)other);
          } else {
            super.mergeFrom(other);
            return this;
          }
        }

        public Builder mergeFrom(MessageModel.FindNodeMessage.FindNode other) {
          if (other == MessageModel.FindNodeMessage.FindNode.getDefaultInstance()) return this;
          if (other.getTarget() != com.google.protobuf.ByteString.EMPTY) {
            setTarget(other.getTarget());
          }
          if (other.getExpires() != 0L) {
            setExpires(other.getExpires());
          }
          this.mergeUnknownFields(other.unknownFields);
          onChanged();
          return this;
        }

        @java.lang.Override
        public final boolean isInitialized() {
          return true;
        }

        @java.lang.Override
        public Builder mergeFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          MessageModel.FindNodeMessage.FindNode parsedMessage = null;
          try {
            parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            parsedMessage = (MessageModel.FindNodeMessage.FindNode) e.getUnfinishedMessage();
            throw e.unwrapIOException();
          } finally {
            if (parsedMessage != null) {
              mergeFrom(parsedMessage);
            }
          }
          return this;
        }

        private com.google.protobuf.ByteString target_ = com.google.protobuf.ByteString.EMPTY;
        /**
         * <code>bytes target = 1;</code>
         */
        public com.google.protobuf.ByteString getTarget() {
          return target_;
        }
        /**
         * <code>bytes target = 1;</code>
         */
        public Builder setTarget(com.google.protobuf.ByteString value) {
          if (value == null) {
    throw new NullPointerException();
  }

          target_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>bytes target = 1;</code>
         */
        public Builder clearTarget() {

          target_ = getDefaultInstance().getTarget();
          onChanged();
          return this;
        }

        private long expires_ ;
        /**
         * <code>uint64 expires = 2;</code>
         */
        public long getExpires() {
          return expires_;
        }
        /**
         * <code>uint64 expires = 2;</code>
         */
        public Builder setExpires(long value) {

          expires_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>uint64 expires = 2;</code>
         */
        public Builder clearExpires() {

          expires_ = 0L;
          onChanged();
          return this;
        }
        @java.lang.Override
        public final Builder setUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.setUnknownFields(unknownFields);
        }

        @java.lang.Override
        public final Builder mergeUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.mergeUnknownFields(unknownFields);
        }


        // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.FindNodeMessage.FindNode)
      }

      // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.FindNodeMessage.FindNode)
      private static final MessageModel.FindNodeMessage.FindNode DEFAULT_INSTANCE;
      static {
        DEFAULT_INSTANCE = new MessageModel.FindNodeMessage.FindNode();
      }

      public static MessageModel.FindNodeMessage.FindNode getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }

      private static final com.google.protobuf.Parser<FindNode>
          PARSER = new com.google.protobuf.AbstractParser<FindNode>() {
        @java.lang.Override
        public FindNode parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new FindNode(input, extensionRegistry);
        }
      };

      public static com.google.protobuf.Parser<FindNode> parser() {
        return PARSER;
      }

      @java.lang.Override
      public com.google.protobuf.Parser<FindNode> getParserForType() {
        return PARSER;
      }

      @java.lang.Override
      public MessageModel.FindNodeMessage.FindNode getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }

    }

    public static final int MAGIC_FIELD_NUMBER = 1;
    private volatile java.lang.Object magic_;
    /**
     * <code>string magic = 1;</code>
     */
    public java.lang.String getMagic() {
      java.lang.Object ref = magic_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        magic_ = s;
        return s;
      }
    }
    /**
     * <code>string magic = 1;</code>
     */
    public com.google.protobuf.ByteString
        getMagicBytes() {
      java.lang.Object ref = magic_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        magic_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int LENGTH_FIELD_NUMBER = 2;
    private int length_;
    /**
     * <code>sfixed32 length = 2;</code>
     */
    public int getLength() {
      return length_;
    }

    public static final int CHECKSUM_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString checksum_;
    /**
     * <code>bytes checksum = 3;</code>
     */
    public com.google.protobuf.ByteString getChecksum() {
      return checksum_;
    }

    public static final int FINDNODE_FIELD_NUMBER = 4;
    private MessageModel.FindNodeMessage.FindNode findNode_;
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
     */
    public boolean hasFindNode() {
      return findNode_ != null;
    }
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
     */
    public MessageModel.FindNodeMessage.FindNode getFindNode() {
      return findNode_ == null ? MessageModel.FindNodeMessage.FindNode.getDefaultInstance() : findNode_;
    }
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
     */
    public MessageModel.FindNodeMessage.FindNodeOrBuilder getFindNodeOrBuilder() {
      return getFindNode();
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getMagicBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, magic_);
      }
      if (length_ != 0) {
        output.writeSFixed32(2, length_);
      }
      if (!checksum_.isEmpty()) {
        output.writeBytes(3, checksum_);
      }
      if (findNode_ != null) {
        output.writeMessage(4, getFindNode());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getMagicBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, magic_);
      }
      if (length_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeSFixed32Size(2, length_);
      }
      if (!checksum_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, checksum_);
      }
      if (findNode_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, getFindNode());
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof MessageModel.FindNodeMessage)) {
        return super.equals(obj);
      }
      MessageModel.FindNodeMessage other = (MessageModel.FindNodeMessage) obj;

      if (!getMagic()
          .equals(other.getMagic())) return false;
      if (getLength()
          != other.getLength()) return false;
      if (!getChecksum()
          .equals(other.getChecksum())) return false;
      if (hasFindNode() != other.hasFindNode()) return false;
      if (hasFindNode()) {
        if (!getFindNode()
            .equals(other.getFindNode())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + MAGIC_FIELD_NUMBER;
      hash = (53 * hash) + getMagic().hashCode();
      hash = (37 * hash) + LENGTH_FIELD_NUMBER;
      hash = (53 * hash) + getLength();
      hash = (37 * hash) + CHECKSUM_FIELD_NUMBER;
      hash = (53 * hash) + getChecksum().hashCode();
      if (hasFindNode()) {
        hash = (37 * hash) + FINDNODE_FIELD_NUMBER;
        hash = (53 * hash) + getFindNode().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static MessageModel.FindNodeMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.FindNodeMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.FindNodeMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.FindNodeMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.FindNodeMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.FindNodeMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.FindNodeMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.FindNodeMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.FindNodeMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static MessageModel.FindNodeMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.FindNodeMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.FindNodeMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(MessageModel.FindNodeMessage prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.FindNodeMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.FindNodeMessage)
        MessageModel.FindNodeMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.FindNodeMessage.class, MessageModel.FindNodeMessage.Builder.class);
      }

      // Construct using MessageModel.FindNodeMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        magic_ = "";

        length_ = 0;

        checksum_ = com.google.protobuf.ByteString.EMPTY;

        if (findNodeBuilder_ == null) {
          findNode_ = null;
        } else {
          findNode_ = null;
          findNodeBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return MessageModel.internal_static_org_ethereum_protobuf_FindNodeMessage_descriptor;
      }

      @java.lang.Override
      public MessageModel.FindNodeMessage getDefaultInstanceForType() {
        return MessageModel.FindNodeMessage.getDefaultInstance();
      }

      @java.lang.Override
      public MessageModel.FindNodeMessage build() {
        MessageModel.FindNodeMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public MessageModel.FindNodeMessage buildPartial() {
        MessageModel.FindNodeMessage result = new MessageModel.FindNodeMessage(this);
        result.magic_ = magic_;
        result.length_ = length_;
        result.checksum_ = checksum_;
        if (findNodeBuilder_ == null) {
          result.findNode_ = findNode_;
        } else {
          result.findNode_ = findNodeBuilder_.build();
        }
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof MessageModel.FindNodeMessage) {
          return mergeFrom((MessageModel.FindNodeMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(MessageModel.FindNodeMessage other) {
        if (other == MessageModel.FindNodeMessage.getDefaultInstance()) return this;
        if (!other.getMagic().isEmpty()) {
          magic_ = other.magic_;
          onChanged();
        }
        if (other.getLength() != 0) {
          setLength(other.getLength());
        }
        if (other.getChecksum() != com.google.protobuf.ByteString.EMPTY) {
          setChecksum(other.getChecksum());
        }
        if (other.hasFindNode()) {
          mergeFindNode(other.getFindNode());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        MessageModel.FindNodeMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (MessageModel.FindNodeMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object magic_ = "";
      /**
       * <code>string magic = 1;</code>
       */
      public java.lang.String getMagic() {
        java.lang.Object ref = magic_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          magic_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string magic = 1;</code>
       */
      public com.google.protobuf.ByteString
          getMagicBytes() {
        java.lang.Object ref = magic_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          magic_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder setMagic(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }

        magic_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder clearMagic() {

        magic_ = getDefaultInstance().getMagic();
        onChanged();
        return this;
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder setMagicBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

        magic_ = value;
        onChanged();
        return this;
      }

      private int length_ ;
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public int getLength() {
        return length_;
      }
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public Builder setLength(int value) {

        length_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public Builder clearLength() {

        length_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString checksum_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes checksum = 3;</code>
       */
      public com.google.protobuf.ByteString getChecksum() {
        return checksum_;
      }
      /**
       * <code>bytes checksum = 3;</code>
       */
      public Builder setChecksum(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        checksum_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes checksum = 3;</code>
       */
      public Builder clearChecksum() {

        checksum_ = getDefaultInstance().getChecksum();
        onChanged();
        return this;
      }

      private MessageModel.FindNodeMessage.FindNode findNode_;
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.FindNodeMessage.FindNode, MessageModel.FindNodeMessage.FindNode.Builder, MessageModel.FindNodeMessage.FindNodeOrBuilder> findNodeBuilder_;
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
       */
      public boolean hasFindNode() {
        return findNodeBuilder_ != null || findNode_ != null;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
       */
      public MessageModel.FindNodeMessage.FindNode getFindNode() {
        if (findNodeBuilder_ == null) {
          return findNode_ == null ? MessageModel.FindNodeMessage.FindNode.getDefaultInstance() : findNode_;
        } else {
          return findNodeBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
       */
      public Builder setFindNode(MessageModel.FindNodeMessage.FindNode value) {
        if (findNodeBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          findNode_ = value;
          onChanged();
        } else {
          findNodeBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
       */
      public Builder setFindNode(
          MessageModel.FindNodeMessage.FindNode.Builder builderForValue) {
        if (findNodeBuilder_ == null) {
          findNode_ = builderForValue.build();
          onChanged();
        } else {
          findNodeBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
       */
      public Builder mergeFindNode(MessageModel.FindNodeMessage.FindNode value) {
        if (findNodeBuilder_ == null) {
          if (findNode_ != null) {
            findNode_ =
              MessageModel.FindNodeMessage.FindNode.newBuilder(findNode_).mergeFrom(value).buildPartial();
          } else {
            findNode_ = value;
          }
          onChanged();
        } else {
          findNodeBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
       */
      public Builder clearFindNode() {
        if (findNodeBuilder_ == null) {
          findNode_ = null;
          onChanged();
        } else {
          findNode_ = null;
          findNodeBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
       */
      public MessageModel.FindNodeMessage.FindNode.Builder getFindNodeBuilder() {

        onChanged();
        return getFindNodeFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
       */
      public MessageModel.FindNodeMessage.FindNodeOrBuilder getFindNodeOrBuilder() {
        if (findNodeBuilder_ != null) {
          return findNodeBuilder_.getMessageOrBuilder();
        } else {
          return findNode_ == null ?
              MessageModel.FindNodeMessage.FindNode.getDefaultInstance() : findNode_;
        }
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage.FindNode findNode = 4;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.FindNodeMessage.FindNode, MessageModel.FindNodeMessage.FindNode.Builder, MessageModel.FindNodeMessage.FindNodeOrBuilder>
          getFindNodeFieldBuilder() {
        if (findNodeBuilder_ == null) {
          findNodeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              MessageModel.FindNodeMessage.FindNode, MessageModel.FindNodeMessage.FindNode.Builder, MessageModel.FindNodeMessage.FindNodeOrBuilder>(
                  getFindNode(),
                  getParentForChildren(),
                  isClean());
          findNode_ = null;
        }
        return findNodeBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.FindNodeMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.FindNodeMessage)
    private static final MessageModel.FindNodeMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new MessageModel.FindNodeMessage();
    }

    public static MessageModel.FindNodeMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<FindNodeMessage>
        PARSER = new com.google.protobuf.AbstractParser<FindNodeMessage>() {
      @java.lang.Override
      public FindNodeMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new FindNodeMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<FindNodeMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<FindNodeMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public MessageModel.FindNodeMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface NeighborsMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.NeighborsMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string magic = 1;</code>
     */
    java.lang.String getMagic();
    /**
     * <code>string magic = 1;</code>
     */
    com.google.protobuf.ByteString
        getMagicBytes();

    /**
     * <code>sfixed32 length = 2;</code>
     */
    int getLength();

    /**
     * <code>bytes checksum = 3;</code>
     */
    com.google.protobuf.ByteString getChecksum();

    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
     */
    boolean hasNeighbors();
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
     */
    MessageModel.NeighborsMessage.Neighbors getNeighbors();
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
     */
    MessageModel.NeighborsMessage.NeighborsOrBuilder getNeighborsOrBuilder();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.NeighborsMessage}
   */
  public  static final class NeighborsMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.NeighborsMessage)
      NeighborsMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use NeighborsMessage.newBuilder() to construct.
    private NeighborsMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private NeighborsMessage() {
      magic_ = "";
      checksum_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private NeighborsMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              magic_ = s;
              break;
            }
            case 21: {

              length_ = input.readSFixed32();
              break;
            }
            case 26: {

              checksum_ = input.readBytes();
              break;
            }
            case 34: {
              MessageModel.NeighborsMessage.Neighbors.Builder subBuilder = null;
              if (neighbors_ != null) {
                subBuilder = neighbors_.toBuilder();
              }
              neighbors_ = input.readMessage(MessageModel.NeighborsMessage.Neighbors.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(neighbors_);
                neighbors_ = subBuilder.buildPartial();
              }

              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              MessageModel.NeighborsMessage.class, MessageModel.NeighborsMessage.Builder.class);
    }

    public interface NeighborsOrBuilder extends
        // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.NeighborsMessage.Neighbors)
        com.google.protobuf.MessageOrBuilder {

      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      java.util.List<MessageModel.NeighborsMessage.Neighbors.Node>
          getNodeList();
      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      MessageModel.NeighborsMessage.Neighbors.Node getNode(int index);
      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      int getNodeCount();
      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      java.util.List<? extends MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder>
          getNodeOrBuilderList();
      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder getNodeOrBuilder(
          int index);

      /**
       * <code>uint64 expires = 2;</code>
       */
      long getExpires();
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.NeighborsMessage.Neighbors}
     */
    public  static final class Neighbors extends
        com.google.protobuf.GeneratedMessageV3 implements
        // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.NeighborsMessage.Neighbors)
        NeighborsOrBuilder {
    private static final long serialVersionUID = 0L;
      // Use Neighbors.newBuilder() to construct.
      private Neighbors(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
        super(builder);
      }
      private Neighbors() {
        node_ = java.util.Collections.emptyList();
      }

      @java.lang.Override
      public final com.google.protobuf.UnknownFieldSet
      getUnknownFields() {
        return this.unknownFields;
      }
      private Neighbors(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        this();
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        int mutable_bitField0_ = 0;
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
            com.google.protobuf.UnknownFieldSet.newBuilder();
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 10: {
                if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                  node_ = new java.util.ArrayList<MessageModel.NeighborsMessage.Neighbors.Node>();
                  mutable_bitField0_ |= 0x00000001;
                }
                node_.add(
                    input.readMessage(MessageModel.NeighborsMessage.Neighbors.Node.parser(), extensionRegistry));
                break;
              }
              case 16: {

                expires_ = input.readUInt64();
                break;
              }
              default: {
                if (!parseUnknownField(
                    input, unknownFields, extensionRegistry, tag)) {
                  done = true;
                }
                break;
              }
            }
          }
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(this);
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(
              e).setUnfinishedMessage(this);
        } finally {
          if (((mutable_bitField0_ & 0x00000001) != 0)) {
            node_ = java.util.Collections.unmodifiableList(node_);
          }
          this.unknownFields = unknownFields.build();
          makeExtensionsImmutable();
        }
      }
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.NeighborsMessage.Neighbors.class, MessageModel.NeighborsMessage.Neighbors.Builder.class);
      }

      public interface NodeOrBuilder extends
          // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.NeighborsMessage.Neighbors.Node)
          com.google.protobuf.MessageOrBuilder {

        /**
         * <code>bytes noteid = 1;</code>
         */
        com.google.protobuf.ByteString getNoteid();

        /**
         * <code>string ip = 2;</code>
         */
        java.lang.String getIp();
        /**
         * <code>string ip = 2;</code>
         */
        com.google.protobuf.ByteString
            getIpBytes();

        /**
         * <code>int32 prot = 3;</code>
         */
        int getProt();
      }
      /**
       * Protobuf type {@code org.ethereum.protobuf.NeighborsMessage.Neighbors.Node}
       */
      public  static final class Node extends
          com.google.protobuf.GeneratedMessageV3 implements
          // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.NeighborsMessage.Neighbors.Node)
          NodeOrBuilder {
      private static final long serialVersionUID = 0L;
        // Use Node.newBuilder() to construct.
        private Node(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
          super(builder);
        }
        private Node() {
          noteid_ = com.google.protobuf.ByteString.EMPTY;
          ip_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
          return this.unknownFields;
        }
        private Node(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          this();
          if (extensionRegistry == null) {
            throw new java.lang.NullPointerException();
          }
          int mutable_bitField0_ = 0;
          com.google.protobuf.UnknownFieldSet.Builder unknownFields =
              com.google.protobuf.UnknownFieldSet.newBuilder();
          try {
            boolean done = false;
            while (!done) {
              int tag = input.readTag();
              switch (tag) {
                case 0:
                  done = true;
                  break;
                case 10: {

                  noteid_ = input.readBytes();
                  break;
                }
                case 18: {
                  java.lang.String s = input.readStringRequireUtf8();

                  ip_ = s;
                  break;
                }
                case 24: {

                  prot_ = input.readInt32();
                  break;
                }
                default: {
                  if (!parseUnknownField(
                      input, unknownFields, extensionRegistry, tag)) {
                    done = true;
                  }
                  break;
                }
              }
            }
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(this);
          } catch (java.io.IOException e) {
            throw new com.google.protobuf.InvalidProtocolBufferException(
                e).setUnfinishedMessage(this);
          } finally {
            this.unknownFields = unknownFields.build();
            makeExtensionsImmutable();
          }
        }
        public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
          return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
          return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_fieldAccessorTable
              .ensureFieldAccessorsInitialized(
                  MessageModel.NeighborsMessage.Neighbors.Node.class, MessageModel.NeighborsMessage.Neighbors.Node.Builder.class);
        }

        public static final int NOTEID_FIELD_NUMBER = 1;
        private com.google.protobuf.ByteString noteid_;
        /**
         * <code>bytes noteid = 1;</code>
         */
        public com.google.protobuf.ByteString getNoteid() {
          return noteid_;
        }

        public static final int IP_FIELD_NUMBER = 2;
        private volatile java.lang.Object ip_;
        /**
         * <code>string ip = 2;</code>
         */
        public java.lang.String getIp() {
          java.lang.Object ref = ip_;
          if (ref instanceof java.lang.String) {
            return (java.lang.String) ref;
          } else {
            com.google.protobuf.ByteString bs =
                (com.google.protobuf.ByteString) ref;
            java.lang.String s = bs.toStringUtf8();
            ip_ = s;
            return s;
          }
        }
        /**
         * <code>string ip = 2;</code>
         */
        public com.google.protobuf.ByteString
            getIpBytes() {
          java.lang.Object ref = ip_;
          if (ref instanceof java.lang.String) {
            com.google.protobuf.ByteString b =
                com.google.protobuf.ByteString.copyFromUtf8(
                    (java.lang.String) ref);
            ip_ = b;
            return b;
          } else {
            return (com.google.protobuf.ByteString) ref;
          }
        }

        public static final int PROT_FIELD_NUMBER = 3;
        private int prot_;
        /**
         * <code>int32 prot = 3;</code>
         */
        public int getProt() {
          return prot_;
        }

        private byte memoizedIsInitialized = -1;
        @java.lang.Override
        public final boolean isInitialized() {
          byte isInitialized = memoizedIsInitialized;
          if (isInitialized == 1) return true;
          if (isInitialized == 0) return false;

          memoizedIsInitialized = 1;
          return true;
        }

        @java.lang.Override
        public void writeTo(com.google.protobuf.CodedOutputStream output)
                            throws java.io.IOException {
          if (!noteid_.isEmpty()) {
            output.writeBytes(1, noteid_);
          }
          if (!getIpBytes().isEmpty()) {
            com.google.protobuf.GeneratedMessageV3.writeString(output, 2, ip_);
          }
          if (prot_ != 0) {
            output.writeInt32(3, prot_);
          }
          unknownFields.writeTo(output);
        }

        @java.lang.Override
        public int getSerializedSize() {
          int size = memoizedSize;
          if (size != -1) return size;

          size = 0;
          if (!noteid_.isEmpty()) {
            size += com.google.protobuf.CodedOutputStream
              .computeBytesSize(1, noteid_);
          }
          if (!getIpBytes().isEmpty()) {
            size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, ip_);
          }
          if (prot_ != 0) {
            size += com.google.protobuf.CodedOutputStream
              .computeInt32Size(3, prot_);
          }
          size += unknownFields.getSerializedSize();
          memoizedSize = size;
          return size;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
          if (obj == this) {
           return true;
          }
          if (!(obj instanceof MessageModel.NeighborsMessage.Neighbors.Node)) {
            return super.equals(obj);
          }
          MessageModel.NeighborsMessage.Neighbors.Node other = (MessageModel.NeighborsMessage.Neighbors.Node) obj;

          if (!getNoteid()
              .equals(other.getNoteid())) return false;
          if (!getIp()
              .equals(other.getIp())) return false;
          if (getProt()
              != other.getProt()) return false;
          if (!unknownFields.equals(other.unknownFields)) return false;
          return true;
        }

        @java.lang.Override
        public int hashCode() {
          if (memoizedHashCode != 0) {
            return memoizedHashCode;
          }
          int hash = 41;
          hash = (19 * hash) + getDescriptor().hashCode();
          hash = (37 * hash) + NOTEID_FIELD_NUMBER;
          hash = (53 * hash) + getNoteid().hashCode();
          hash = (37 * hash) + IP_FIELD_NUMBER;
          hash = (53 * hash) + getIp().hashCode();
          hash = (37 * hash) + PROT_FIELD_NUMBER;
          hash = (53 * hash) + getProt();
          hash = (29 * hash) + unknownFields.hashCode();
          memoizedHashCode = hash;
          return hash;
        }

        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(
            java.nio.ByteBuffer data)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(
            java.nio.ByteBuffer data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(
            com.google.protobuf.ByteString data)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(
            com.google.protobuf.ByteString data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(byte[] data)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(
            byte[] data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return PARSER.parseFrom(data, extensionRegistry);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(java.io.InputStream input)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseWithIOException(PARSER, input);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(
            java.io.InputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseWithIOException(PARSER, input, extensionRegistry);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseDelimitedFrom(java.io.InputStream input)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseDelimitedWithIOException(PARSER, input);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseDelimitedFrom(
            java.io.InputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(
            com.google.protobuf.CodedInputStream input)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseWithIOException(PARSER, input);
        }
        public static MessageModel.NeighborsMessage.Neighbors.Node parseFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          return com.google.protobuf.GeneratedMessageV3
              .parseWithIOException(PARSER, input, extensionRegistry);
        }

        @java.lang.Override
        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder() {
          return DEFAULT_INSTANCE.toBuilder();
        }
        public static Builder newBuilder(MessageModel.NeighborsMessage.Neighbors.Node prototype) {
          return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }
        @java.lang.Override
        public Builder toBuilder() {
          return this == DEFAULT_INSTANCE
              ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
          Builder builder = new Builder(parent);
          return builder;
        }
        /**
         * Protobuf type {@code org.ethereum.protobuf.NeighborsMessage.Neighbors.Node}
         */
        public static final class Builder extends
            com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
            // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.NeighborsMessage.Neighbors.Node)
            MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder {
          public static final com.google.protobuf.Descriptors.Descriptor
              getDescriptor() {
            return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_descriptor;
          }

          @java.lang.Override
          protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
              internalGetFieldAccessorTable() {
            return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_fieldAccessorTable
                .ensureFieldAccessorsInitialized(
                    MessageModel.NeighborsMessage.Neighbors.Node.class, MessageModel.NeighborsMessage.Neighbors.Node.Builder.class);
          }

          // Construct using MessageModel.NeighborsMessage.Neighbors.Node.newBuilder()
          private Builder() {
            maybeForceBuilderInitialization();
          }

          private Builder(
              com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            maybeForceBuilderInitialization();
          }
          private void maybeForceBuilderInitialization() {
            if (com.google.protobuf.GeneratedMessageV3
                    .alwaysUseFieldBuilders) {
            }
          }
          @java.lang.Override
          public Builder clear() {
            super.clear();
            noteid_ = com.google.protobuf.ByteString.EMPTY;

            ip_ = "";

            prot_ = 0;

            return this;
          }

          @java.lang.Override
          public com.google.protobuf.Descriptors.Descriptor
              getDescriptorForType() {
            return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_descriptor;
          }

          @java.lang.Override
          public MessageModel.NeighborsMessage.Neighbors.Node getDefaultInstanceForType() {
            return MessageModel.NeighborsMessage.Neighbors.Node.getDefaultInstance();
          }

          @java.lang.Override
          public MessageModel.NeighborsMessage.Neighbors.Node build() {
            MessageModel.NeighborsMessage.Neighbors.Node result = buildPartial();
            if (!result.isInitialized()) {
              throw newUninitializedMessageException(result);
            }
            return result;
          }

          @java.lang.Override
          public MessageModel.NeighborsMessage.Neighbors.Node buildPartial() {
            MessageModel.NeighborsMessage.Neighbors.Node result = new MessageModel.NeighborsMessage.Neighbors.Node(this);
            result.noteid_ = noteid_;
            result.ip_ = ip_;
            result.prot_ = prot_;
            onBuilt();
            return result;
          }

          @java.lang.Override
          public Builder clone() {
            return super.clone();
          }
          @java.lang.Override
          public Builder setField(
              com.google.protobuf.Descriptors.FieldDescriptor field,
              java.lang.Object value) {
            return super.setField(field, value);
          }
          @java.lang.Override
          public Builder clearField(
              com.google.protobuf.Descriptors.FieldDescriptor field) {
            return super.clearField(field);
          }
          @java.lang.Override
          public Builder clearOneof(
              com.google.protobuf.Descriptors.OneofDescriptor oneof) {
            return super.clearOneof(oneof);
          }
          @java.lang.Override
          public Builder setRepeatedField(
              com.google.protobuf.Descriptors.FieldDescriptor field,
              int index, java.lang.Object value) {
            return super.setRepeatedField(field, index, value);
          }
          @java.lang.Override
          public Builder addRepeatedField(
              com.google.protobuf.Descriptors.FieldDescriptor field,
              java.lang.Object value) {
            return super.addRepeatedField(field, value);
          }
          @java.lang.Override
          public Builder mergeFrom(com.google.protobuf.Message other) {
            if (other instanceof MessageModel.NeighborsMessage.Neighbors.Node) {
              return mergeFrom((MessageModel.NeighborsMessage.Neighbors.Node)other);
            } else {
              super.mergeFrom(other);
              return this;
            }
          }

          public Builder mergeFrom(MessageModel.NeighborsMessage.Neighbors.Node other) {
            if (other == MessageModel.NeighborsMessage.Neighbors.Node.getDefaultInstance()) return this;
            if (other.getNoteid() != com.google.protobuf.ByteString.EMPTY) {
              setNoteid(other.getNoteid());
            }
            if (!other.getIp().isEmpty()) {
              ip_ = other.ip_;
              onChanged();
            }
            if (other.getProt() != 0) {
              setProt(other.getProt());
            }
            this.mergeUnknownFields(other.unknownFields);
            onChanged();
            return this;
          }

          @java.lang.Override
          public final boolean isInitialized() {
            return true;
          }

          @java.lang.Override
          public Builder mergeFrom(
              com.google.protobuf.CodedInputStream input,
              com.google.protobuf.ExtensionRegistryLite extensionRegistry)
              throws java.io.IOException {
            MessageModel.NeighborsMessage.Neighbors.Node parsedMessage = null;
            try {
              parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
              parsedMessage = (MessageModel.NeighborsMessage.Neighbors.Node) e.getUnfinishedMessage();
              throw e.unwrapIOException();
            } finally {
              if (parsedMessage != null) {
                mergeFrom(parsedMessage);
              }
            }
            return this;
          }

          private com.google.protobuf.ByteString noteid_ = com.google.protobuf.ByteString.EMPTY;
          /**
           * <code>bytes noteid = 1;</code>
           */
          public com.google.protobuf.ByteString getNoteid() {
            return noteid_;
          }
          /**
           * <code>bytes noteid = 1;</code>
           */
          public Builder setNoteid(com.google.protobuf.ByteString value) {
            if (value == null) {
    throw new NullPointerException();
  }

            noteid_ = value;
            onChanged();
            return this;
          }
          /**
           * <code>bytes noteid = 1;</code>
           */
          public Builder clearNoteid() {

            noteid_ = getDefaultInstance().getNoteid();
            onChanged();
            return this;
          }

          private java.lang.Object ip_ = "";
          /**
           * <code>string ip = 2;</code>
           */
          public java.lang.String getIp() {
            java.lang.Object ref = ip_;
            if (!(ref instanceof java.lang.String)) {
              com.google.protobuf.ByteString bs =
                  (com.google.protobuf.ByteString) ref;
              java.lang.String s = bs.toStringUtf8();
              ip_ = s;
              return s;
            } else {
              return (java.lang.String) ref;
            }
          }
          /**
           * <code>string ip = 2;</code>
           */
          public com.google.protobuf.ByteString
              getIpBytes() {
            java.lang.Object ref = ip_;
            if (ref instanceof String) {
              com.google.protobuf.ByteString b =
                  com.google.protobuf.ByteString.copyFromUtf8(
                      (java.lang.String) ref);
              ip_ = b;
              return b;
            } else {
              return (com.google.protobuf.ByteString) ref;
            }
          }
          /**
           * <code>string ip = 2;</code>
           */
          public Builder setIp(
              java.lang.String value) {
            if (value == null) {
    throw new NullPointerException();
  }

            ip_ = value;
            onChanged();
            return this;
          }
          /**
           * <code>string ip = 2;</code>
           */
          public Builder clearIp() {

            ip_ = getDefaultInstance().getIp();
            onChanged();
            return this;
          }
          /**
           * <code>string ip = 2;</code>
           */
          public Builder setIpBytes(
              com.google.protobuf.ByteString value) {
            if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

            ip_ = value;
            onChanged();
            return this;
          }

          private int prot_ ;
          /**
           * <code>int32 prot = 3;</code>
           */
          public int getProt() {
            return prot_;
          }
          /**
           * <code>int32 prot = 3;</code>
           */
          public Builder setProt(int value) {

            prot_ = value;
            onChanged();
            return this;
          }
          /**
           * <code>int32 prot = 3;</code>
           */
          public Builder clearProt() {

            prot_ = 0;
            onChanged();
            return this;
          }
          @java.lang.Override
          public final Builder setUnknownFields(
              final com.google.protobuf.UnknownFieldSet unknownFields) {
            return super.setUnknownFields(unknownFields);
          }

          @java.lang.Override
          public final Builder mergeUnknownFields(
              final com.google.protobuf.UnknownFieldSet unknownFields) {
            return super.mergeUnknownFields(unknownFields);
          }


          // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.NeighborsMessage.Neighbors.Node)
        }

        // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.NeighborsMessage.Neighbors.Node)
        private static final MessageModel.NeighborsMessage.Neighbors.Node DEFAULT_INSTANCE;
        static {
          DEFAULT_INSTANCE = new MessageModel.NeighborsMessage.Neighbors.Node();
        }

        public static MessageModel.NeighborsMessage.Neighbors.Node getDefaultInstance() {
          return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<Node>
            PARSER = new com.google.protobuf.AbstractParser<Node>() {
          @java.lang.Override
          public Node parsePartialFrom(
              com.google.protobuf.CodedInputStream input,
              com.google.protobuf.ExtensionRegistryLite extensionRegistry)
              throws com.google.protobuf.InvalidProtocolBufferException {
            return new Node(input, extensionRegistry);
          }
        };

        public static com.google.protobuf.Parser<Node> parser() {
          return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<Node> getParserForType() {
          return PARSER;
        }

        @java.lang.Override
        public MessageModel.NeighborsMessage.Neighbors.Node getDefaultInstanceForType() {
          return DEFAULT_INSTANCE;
        }

      }

      private int bitField0_;
      public static final int NODE_FIELD_NUMBER = 1;
      private java.util.List<MessageModel.NeighborsMessage.Neighbors.Node> node_;
      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      public java.util.List<MessageModel.NeighborsMessage.Neighbors.Node> getNodeList() {
        return node_;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      public java.util.List<? extends MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder>
          getNodeOrBuilderList() {
        return node_;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      public int getNodeCount() {
        return node_.size();
      }
      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      public MessageModel.NeighborsMessage.Neighbors.Node getNode(int index) {
        return node_.get(index);
      }
      /**
       * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
       */
      public MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder getNodeOrBuilder(
          int index) {
        return node_.get(index);
      }

      public static final int EXPIRES_FIELD_NUMBER = 2;
      private long expires_;
      /**
       * <code>uint64 expires = 2;</code>
       */
      public long getExpires() {
        return expires_;
      }

      private byte memoizedIsInitialized = -1;
      @java.lang.Override
      public final boolean isInitialized() {
        byte isInitialized = memoizedIsInitialized;
        if (isInitialized == 1) return true;
        if (isInitialized == 0) return false;

        memoizedIsInitialized = 1;
        return true;
      }

      @java.lang.Override
      public void writeTo(com.google.protobuf.CodedOutputStream output)
                          throws java.io.IOException {
        for (int i = 0; i < node_.size(); i++) {
          output.writeMessage(1, node_.get(i));
        }
        if (expires_ != 0L) {
          output.writeUInt64(2, expires_);
        }
        unknownFields.writeTo(output);
      }

      @java.lang.Override
      public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1) return size;

        size = 0;
        for (int i = 0; i < node_.size(); i++) {
          size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(1, node_.get(i));
        }
        if (expires_ != 0L) {
          size += com.google.protobuf.CodedOutputStream
            .computeUInt64Size(2, expires_);
        }
        size += unknownFields.getSerializedSize();
        memoizedSize = size;
        return size;
      }

      @java.lang.Override
      public boolean equals(final java.lang.Object obj) {
        if (obj == this) {
         return true;
        }
        if (!(obj instanceof MessageModel.NeighborsMessage.Neighbors)) {
          return super.equals(obj);
        }
        MessageModel.NeighborsMessage.Neighbors other = (MessageModel.NeighborsMessage.Neighbors) obj;

        if (!getNodeList()
            .equals(other.getNodeList())) return false;
        if (getExpires()
            != other.getExpires()) return false;
        if (!unknownFields.equals(other.unknownFields)) return false;
        return true;
      }

      @java.lang.Override
      public int hashCode() {
        if (memoizedHashCode != 0) {
          return memoizedHashCode;
        }
        int hash = 41;
        hash = (19 * hash) + getDescriptor().hashCode();
        if (getNodeCount() > 0) {
          hash = (37 * hash) + NODE_FIELD_NUMBER;
          hash = (53 * hash) + getNodeList().hashCode();
        }
        hash = (37 * hash) + EXPIRES_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getExpires());
        hash = (29 * hash) + unknownFields.hashCode();
        memoizedHashCode = hash;
        return hash;
      }

      public static MessageModel.NeighborsMessage.Neighbors parseFrom(
          java.nio.ByteBuffer data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseFrom(
          java.nio.ByteBuffer data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseFrom(
          com.google.protobuf.ByteString data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseFrom(
          com.google.protobuf.ByteString data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseFrom(byte[] data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseFrom(
          byte[] data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseDelimitedFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseDelimitedFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseFrom(
          com.google.protobuf.CodedInputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.NeighborsMessage.Neighbors parseFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }

      @java.lang.Override
      public Builder newBuilderForType() { return newBuilder(); }
      public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
      }
      public static Builder newBuilder(MessageModel.NeighborsMessage.Neighbors prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }
      @java.lang.Override
      public Builder toBuilder() {
        return this == DEFAULT_INSTANCE
            ? new Builder() : new Builder().mergeFrom(this);
      }

      @java.lang.Override
      protected Builder newBuilderForType(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
      }
      /**
       * Protobuf type {@code org.ethereum.protobuf.NeighborsMessage.Neighbors}
       */
      public static final class Builder extends
          com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
          // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.NeighborsMessage.Neighbors)
          MessageModel.NeighborsMessage.NeighborsOrBuilder {
        public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
          return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
          return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_fieldAccessorTable
              .ensureFieldAccessorsInitialized(
                  MessageModel.NeighborsMessage.Neighbors.class, MessageModel.NeighborsMessage.Neighbors.Builder.class);
        }

        // Construct using MessageModel.NeighborsMessage.Neighbors.newBuilder()
        private Builder() {
          maybeForceBuilderInitialization();
        }

        private Builder(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
          super(parent);
          maybeForceBuilderInitialization();
        }
        private void maybeForceBuilderInitialization() {
          if (com.google.protobuf.GeneratedMessageV3
                  .alwaysUseFieldBuilders) {
            getNodeFieldBuilder();
          }
        }
        @java.lang.Override
        public Builder clear() {
          super.clear();
          if (nodeBuilder_ == null) {
            node_ = java.util.Collections.emptyList();
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            nodeBuilder_.clear();
          }
          expires_ = 0L;

          return this;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
          return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_descriptor;
        }

        @java.lang.Override
        public MessageModel.NeighborsMessage.Neighbors getDefaultInstanceForType() {
          return MessageModel.NeighborsMessage.Neighbors.getDefaultInstance();
        }

        @java.lang.Override
        public MessageModel.NeighborsMessage.Neighbors build() {
          MessageModel.NeighborsMessage.Neighbors result = buildPartial();
          if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
          }
          return result;
        }

        @java.lang.Override
        public MessageModel.NeighborsMessage.Neighbors buildPartial() {
          MessageModel.NeighborsMessage.Neighbors result = new MessageModel.NeighborsMessage.Neighbors(this);
          int from_bitField0_ = bitField0_;
          int to_bitField0_ = 0;
          if (nodeBuilder_ == null) {
            if (((bitField0_ & 0x00000001) != 0)) {
              node_ = java.util.Collections.unmodifiableList(node_);
              bitField0_ = (bitField0_ & ~0x00000001);
            }
            result.node_ = node_;
          } else {
            result.node_ = nodeBuilder_.build();
          }
          result.expires_ = expires_;
          result.bitField0_ = to_bitField0_;
          onBuilt();
          return result;
        }

        @java.lang.Override
        public Builder clone() {
          return super.clone();
        }
        @java.lang.Override
        public Builder setField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.setField(field, value);
        }
        @java.lang.Override
        public Builder clearField(
            com.google.protobuf.Descriptors.FieldDescriptor field) {
          return super.clearField(field);
        }
        @java.lang.Override
        public Builder clearOneof(
            com.google.protobuf.Descriptors.OneofDescriptor oneof) {
          return super.clearOneof(oneof);
        }
        @java.lang.Override
        public Builder setRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            int index, java.lang.Object value) {
          return super.setRepeatedField(field, index, value);
        }
        @java.lang.Override
        public Builder addRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.addRepeatedField(field, value);
        }
        @java.lang.Override
        public Builder mergeFrom(com.google.protobuf.Message other) {
          if (other instanceof MessageModel.NeighborsMessage.Neighbors) {
            return mergeFrom((MessageModel.NeighborsMessage.Neighbors)other);
          } else {
            super.mergeFrom(other);
            return this;
          }
        }

        public Builder mergeFrom(MessageModel.NeighborsMessage.Neighbors other) {
          if (other == MessageModel.NeighborsMessage.Neighbors.getDefaultInstance()) return this;
          if (nodeBuilder_ == null) {
            if (!other.node_.isEmpty()) {
              if (node_.isEmpty()) {
                node_ = other.node_;
                bitField0_ = (bitField0_ & ~0x00000001);
              } else {
                ensureNodeIsMutable();
                node_.addAll(other.node_);
              }
              onChanged();
            }
          } else {
            if (!other.node_.isEmpty()) {
              if (nodeBuilder_.isEmpty()) {
                nodeBuilder_.dispose();
                nodeBuilder_ = null;
                node_ = other.node_;
                bitField0_ = (bitField0_ & ~0x00000001);
                nodeBuilder_ =
                  com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                     getNodeFieldBuilder() : null;
              } else {
                nodeBuilder_.addAllMessages(other.node_);
              }
            }
          }
          if (other.getExpires() != 0L) {
            setExpires(other.getExpires());
          }
          this.mergeUnknownFields(other.unknownFields);
          onChanged();
          return this;
        }

        @java.lang.Override
        public final boolean isInitialized() {
          return true;
        }

        @java.lang.Override
        public Builder mergeFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          MessageModel.NeighborsMessage.Neighbors parsedMessage = null;
          try {
            parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            parsedMessage = (MessageModel.NeighborsMessage.Neighbors) e.getUnfinishedMessage();
            throw e.unwrapIOException();
          } finally {
            if (parsedMessage != null) {
              mergeFrom(parsedMessage);
            }
          }
          return this;
        }
        private int bitField0_;

        private java.util.List<MessageModel.NeighborsMessage.Neighbors.Node> node_ =
          java.util.Collections.emptyList();
        private void ensureNodeIsMutable() {
          if (!((bitField0_ & 0x00000001) != 0)) {
            node_ = new java.util.ArrayList<MessageModel.NeighborsMessage.Neighbors.Node>(node_);
            bitField0_ |= 0x00000001;
           }
        }

        private com.google.protobuf.RepeatedFieldBuilderV3<
            MessageModel.NeighborsMessage.Neighbors.Node, MessageModel.NeighborsMessage.Neighbors.Node.Builder, MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder> nodeBuilder_;

        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public java.util.List<MessageModel.NeighborsMessage.Neighbors.Node> getNodeList() {
          if (nodeBuilder_ == null) {
            return java.util.Collections.unmodifiableList(node_);
          } else {
            return nodeBuilder_.getMessageList();
          }
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public int getNodeCount() {
          if (nodeBuilder_ == null) {
            return node_.size();
          } else {
            return nodeBuilder_.getCount();
          }
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public MessageModel.NeighborsMessage.Neighbors.Node getNode(int index) {
          if (nodeBuilder_ == null) {
            return node_.get(index);
          } else {
            return nodeBuilder_.getMessage(index);
          }
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public Builder setNode(
            int index, MessageModel.NeighborsMessage.Neighbors.Node value) {
          if (nodeBuilder_ == null) {
            if (value == null) {
              throw new NullPointerException();
            }
            ensureNodeIsMutable();
            node_.set(index, value);
            onChanged();
          } else {
            nodeBuilder_.setMessage(index, value);
          }
          return this;
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public Builder setNode(
            int index, MessageModel.NeighborsMessage.Neighbors.Node.Builder builderForValue) {
          if (nodeBuilder_ == null) {
            ensureNodeIsMutable();
            node_.set(index, builderForValue.build());
            onChanged();
          } else {
            nodeBuilder_.setMessage(index, builderForValue.build());
          }
          return this;
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public Builder addNode(MessageModel.NeighborsMessage.Neighbors.Node value) {
          if (nodeBuilder_ == null) {
            if (value == null) {
              throw new NullPointerException();
            }
            ensureNodeIsMutable();
            node_.add(value);
            onChanged();
          } else {
            nodeBuilder_.addMessage(value);
          }
          return this;
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public Builder addNode(
            int index, MessageModel.NeighborsMessage.Neighbors.Node value) {
          if (nodeBuilder_ == null) {
            if (value == null) {
              throw new NullPointerException();
            }
            ensureNodeIsMutable();
            node_.add(index, value);
            onChanged();
          } else {
            nodeBuilder_.addMessage(index, value);
          }
          return this;
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public Builder addNode(
            MessageModel.NeighborsMessage.Neighbors.Node.Builder builderForValue) {
          if (nodeBuilder_ == null) {
            ensureNodeIsMutable();
            node_.add(builderForValue.build());
            onChanged();
          } else {
            nodeBuilder_.addMessage(builderForValue.build());
          }
          return this;
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public Builder addNode(
            int index, MessageModel.NeighborsMessage.Neighbors.Node.Builder builderForValue) {
          if (nodeBuilder_ == null) {
            ensureNodeIsMutable();
            node_.add(index, builderForValue.build());
            onChanged();
          } else {
            nodeBuilder_.addMessage(index, builderForValue.build());
          }
          return this;
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public Builder addAllNode(
            java.lang.Iterable<? extends MessageModel.NeighborsMessage.Neighbors.Node> values) {
          if (nodeBuilder_ == null) {
            ensureNodeIsMutable();
            com.google.protobuf.AbstractMessageLite.Builder.addAll(
                values, node_);
            onChanged();
          } else {
            nodeBuilder_.addAllMessages(values);
          }
          return this;
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public Builder clearNode() {
          if (nodeBuilder_ == null) {
            node_ = java.util.Collections.emptyList();
            bitField0_ = (bitField0_ & ~0x00000001);
            onChanged();
          } else {
            nodeBuilder_.clear();
          }
          return this;
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public Builder removeNode(int index) {
          if (nodeBuilder_ == null) {
            ensureNodeIsMutable();
            node_.remove(index);
            onChanged();
          } else {
            nodeBuilder_.remove(index);
          }
          return this;
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public MessageModel.NeighborsMessage.Neighbors.Node.Builder getNodeBuilder(
            int index) {
          return getNodeFieldBuilder().getBuilder(index);
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder getNodeOrBuilder(
            int index) {
          if (nodeBuilder_ == null) {
            return node_.get(index);  } else {
            return nodeBuilder_.getMessageOrBuilder(index);
          }
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public java.util.List<? extends MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder>
             getNodeOrBuilderList() {
          if (nodeBuilder_ != null) {
            return nodeBuilder_.getMessageOrBuilderList();
          } else {
            return java.util.Collections.unmodifiableList(node_);
          }
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public MessageModel.NeighborsMessage.Neighbors.Node.Builder addNodeBuilder() {
          return getNodeFieldBuilder().addBuilder(
              MessageModel.NeighborsMessage.Neighbors.Node.getDefaultInstance());
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public MessageModel.NeighborsMessage.Neighbors.Node.Builder addNodeBuilder(
            int index) {
          return getNodeFieldBuilder().addBuilder(
              index, MessageModel.NeighborsMessage.Neighbors.Node.getDefaultInstance());
        }
        /**
         * <code>repeated .org.ethereum.protobuf.NeighborsMessage.Neighbors.Node node = 1;</code>
         */
        public java.util.List<MessageModel.NeighborsMessage.Neighbors.Node.Builder>
             getNodeBuilderList() {
          return getNodeFieldBuilder().getBuilderList();
        }
        private com.google.protobuf.RepeatedFieldBuilderV3<
            MessageModel.NeighborsMessage.Neighbors.Node, MessageModel.NeighborsMessage.Neighbors.Node.Builder, MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder>
            getNodeFieldBuilder() {
          if (nodeBuilder_ == null) {
            nodeBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
                MessageModel.NeighborsMessage.Neighbors.Node, MessageModel.NeighborsMessage.Neighbors.Node.Builder, MessageModel.NeighborsMessage.Neighbors.NodeOrBuilder>(
                    node_,
                    ((bitField0_ & 0x00000001) != 0),
                    getParentForChildren(),
                    isClean());
            node_ = null;
          }
          return nodeBuilder_;
        }

        private long expires_ ;
        /**
         * <code>uint64 expires = 2;</code>
         */
        public long getExpires() {
          return expires_;
        }
        /**
         * <code>uint64 expires = 2;</code>
         */
        public Builder setExpires(long value) {

          expires_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>uint64 expires = 2;</code>
         */
        public Builder clearExpires() {

          expires_ = 0L;
          onChanged();
          return this;
        }
        @java.lang.Override
        public final Builder setUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.setUnknownFields(unknownFields);
        }

        @java.lang.Override
        public final Builder mergeUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.mergeUnknownFields(unknownFields);
        }


        // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.NeighborsMessage.Neighbors)
      }

      // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.NeighborsMessage.Neighbors)
      private static final MessageModel.NeighborsMessage.Neighbors DEFAULT_INSTANCE;
      static {
        DEFAULT_INSTANCE = new MessageModel.NeighborsMessage.Neighbors();
      }

      public static MessageModel.NeighborsMessage.Neighbors getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }

      private static final com.google.protobuf.Parser<Neighbors>
          PARSER = new com.google.protobuf.AbstractParser<Neighbors>() {
        @java.lang.Override
        public Neighbors parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new Neighbors(input, extensionRegistry);
        }
      };

      public static com.google.protobuf.Parser<Neighbors> parser() {
        return PARSER;
      }

      @java.lang.Override
      public com.google.protobuf.Parser<Neighbors> getParserForType() {
        return PARSER;
      }

      @java.lang.Override
      public MessageModel.NeighborsMessage.Neighbors getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }

    }

    public static final int MAGIC_FIELD_NUMBER = 1;
    private volatile java.lang.Object magic_;
    /**
     * <code>string magic = 1;</code>
     */
    public java.lang.String getMagic() {
      java.lang.Object ref = magic_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        magic_ = s;
        return s;
      }
    }
    /**
     * <code>string magic = 1;</code>
     */
    public com.google.protobuf.ByteString
        getMagicBytes() {
      java.lang.Object ref = magic_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        magic_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int LENGTH_FIELD_NUMBER = 2;
    private int length_;
    /**
     * <code>sfixed32 length = 2;</code>
     */
    public int getLength() {
      return length_;
    }

    public static final int CHECKSUM_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString checksum_;
    /**
     * <code>bytes checksum = 3;</code>
     */
    public com.google.protobuf.ByteString getChecksum() {
      return checksum_;
    }

    public static final int NEIGHBORS_FIELD_NUMBER = 4;
    private MessageModel.NeighborsMessage.Neighbors neighbors_;
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
     */
    public boolean hasNeighbors() {
      return neighbors_ != null;
    }
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
     */
    public MessageModel.NeighborsMessage.Neighbors getNeighbors() {
      return neighbors_ == null ? MessageModel.NeighborsMessage.Neighbors.getDefaultInstance() : neighbors_;
    }
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
     */
    public MessageModel.NeighborsMessage.NeighborsOrBuilder getNeighborsOrBuilder() {
      return getNeighbors();
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getMagicBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, magic_);
      }
      if (length_ != 0) {
        output.writeSFixed32(2, length_);
      }
      if (!checksum_.isEmpty()) {
        output.writeBytes(3, checksum_);
      }
      if (neighbors_ != null) {
        output.writeMessage(4, getNeighbors());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getMagicBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, magic_);
      }
      if (length_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeSFixed32Size(2, length_);
      }
      if (!checksum_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, checksum_);
      }
      if (neighbors_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, getNeighbors());
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof MessageModel.NeighborsMessage)) {
        return super.equals(obj);
      }
      MessageModel.NeighborsMessage other = (MessageModel.NeighborsMessage) obj;

      if (!getMagic()
          .equals(other.getMagic())) return false;
      if (getLength()
          != other.getLength()) return false;
      if (!getChecksum()
          .equals(other.getChecksum())) return false;
      if (hasNeighbors() != other.hasNeighbors()) return false;
      if (hasNeighbors()) {
        if (!getNeighbors()
            .equals(other.getNeighbors())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + MAGIC_FIELD_NUMBER;
      hash = (53 * hash) + getMagic().hashCode();
      hash = (37 * hash) + LENGTH_FIELD_NUMBER;
      hash = (53 * hash) + getLength();
      hash = (37 * hash) + CHECKSUM_FIELD_NUMBER;
      hash = (53 * hash) + getChecksum().hashCode();
      if (hasNeighbors()) {
        hash = (37 * hash) + NEIGHBORS_FIELD_NUMBER;
        hash = (53 * hash) + getNeighbors().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static MessageModel.NeighborsMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.NeighborsMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.NeighborsMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.NeighborsMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.NeighborsMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.NeighborsMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.NeighborsMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.NeighborsMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.NeighborsMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static MessageModel.NeighborsMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.NeighborsMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.NeighborsMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(MessageModel.NeighborsMessage prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.NeighborsMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.NeighborsMessage)
        MessageModel.NeighborsMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.NeighborsMessage.class, MessageModel.NeighborsMessage.Builder.class);
      }

      // Construct using MessageModel.NeighborsMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        magic_ = "";

        length_ = 0;

        checksum_ = com.google.protobuf.ByteString.EMPTY;

        if (neighborsBuilder_ == null) {
          neighbors_ = null;
        } else {
          neighbors_ = null;
          neighborsBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return MessageModel.internal_static_org_ethereum_protobuf_NeighborsMessage_descriptor;
      }

      @java.lang.Override
      public MessageModel.NeighborsMessage getDefaultInstanceForType() {
        return MessageModel.NeighborsMessage.getDefaultInstance();
      }

      @java.lang.Override
      public MessageModel.NeighborsMessage build() {
        MessageModel.NeighborsMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public MessageModel.NeighborsMessage buildPartial() {
        MessageModel.NeighborsMessage result = new MessageModel.NeighborsMessage(this);
        result.magic_ = magic_;
        result.length_ = length_;
        result.checksum_ = checksum_;
        if (neighborsBuilder_ == null) {
          result.neighbors_ = neighbors_;
        } else {
          result.neighbors_ = neighborsBuilder_.build();
        }
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof MessageModel.NeighborsMessage) {
          return mergeFrom((MessageModel.NeighborsMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(MessageModel.NeighborsMessage other) {
        if (other == MessageModel.NeighborsMessage.getDefaultInstance()) return this;
        if (!other.getMagic().isEmpty()) {
          magic_ = other.magic_;
          onChanged();
        }
        if (other.getLength() != 0) {
          setLength(other.getLength());
        }
        if (other.getChecksum() != com.google.protobuf.ByteString.EMPTY) {
          setChecksum(other.getChecksum());
        }
        if (other.hasNeighbors()) {
          mergeNeighbors(other.getNeighbors());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        MessageModel.NeighborsMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (MessageModel.NeighborsMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object magic_ = "";
      /**
       * <code>string magic = 1;</code>
       */
      public java.lang.String getMagic() {
        java.lang.Object ref = magic_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          magic_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string magic = 1;</code>
       */
      public com.google.protobuf.ByteString
          getMagicBytes() {
        java.lang.Object ref = magic_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          magic_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder setMagic(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }

        magic_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder clearMagic() {

        magic_ = getDefaultInstance().getMagic();
        onChanged();
        return this;
      }
      /**
       * <code>string magic = 1;</code>
       */
      public Builder setMagicBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

        magic_ = value;
        onChanged();
        return this;
      }

      private int length_ ;
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public int getLength() {
        return length_;
      }
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public Builder setLength(int value) {

        length_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>sfixed32 length = 2;</code>
       */
      public Builder clearLength() {

        length_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString checksum_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes checksum = 3;</code>
       */
      public com.google.protobuf.ByteString getChecksum() {
        return checksum_;
      }
      /**
       * <code>bytes checksum = 3;</code>
       */
      public Builder setChecksum(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        checksum_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes checksum = 3;</code>
       */
      public Builder clearChecksum() {

        checksum_ = getDefaultInstance().getChecksum();
        onChanged();
        return this;
      }

      private MessageModel.NeighborsMessage.Neighbors neighbors_;
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.NeighborsMessage.Neighbors, MessageModel.NeighborsMessage.Neighbors.Builder, MessageModel.NeighborsMessage.NeighborsOrBuilder> neighborsBuilder_;
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
       */
      public boolean hasNeighbors() {
        return neighborsBuilder_ != null || neighbors_ != null;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
       */
      public MessageModel.NeighborsMessage.Neighbors getNeighbors() {
        if (neighborsBuilder_ == null) {
          return neighbors_ == null ? MessageModel.NeighborsMessage.Neighbors.getDefaultInstance() : neighbors_;
        } else {
          return neighborsBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
       */
      public Builder setNeighbors(MessageModel.NeighborsMessage.Neighbors value) {
        if (neighborsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          neighbors_ = value;
          onChanged();
        } else {
          neighborsBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
       */
      public Builder setNeighbors(
          MessageModel.NeighborsMessage.Neighbors.Builder builderForValue) {
        if (neighborsBuilder_ == null) {
          neighbors_ = builderForValue.build();
          onChanged();
        } else {
          neighborsBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
       */
      public Builder mergeNeighbors(MessageModel.NeighborsMessage.Neighbors value) {
        if (neighborsBuilder_ == null) {
          if (neighbors_ != null) {
            neighbors_ =
              MessageModel.NeighborsMessage.Neighbors.newBuilder(neighbors_).mergeFrom(value).buildPartial();
          } else {
            neighbors_ = value;
          }
          onChanged();
        } else {
          neighborsBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
       */
      public Builder clearNeighbors() {
        if (neighborsBuilder_ == null) {
          neighbors_ = null;
          onChanged();
        } else {
          neighbors_ = null;
          neighborsBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
       */
      public MessageModel.NeighborsMessage.Neighbors.Builder getNeighborsBuilder() {

        onChanged();
        return getNeighborsFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
       */
      public MessageModel.NeighborsMessage.NeighborsOrBuilder getNeighborsOrBuilder() {
        if (neighborsBuilder_ != null) {
          return neighborsBuilder_.getMessageOrBuilder();
        } else {
          return neighbors_ == null ?
              MessageModel.NeighborsMessage.Neighbors.getDefaultInstance() : neighbors_;
        }
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage.Neighbors neighbors = 4;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.NeighborsMessage.Neighbors, MessageModel.NeighborsMessage.Neighbors.Builder, MessageModel.NeighborsMessage.NeighborsOrBuilder>
          getNeighborsFieldBuilder() {
        if (neighborsBuilder_ == null) {
          neighborsBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              MessageModel.NeighborsMessage.Neighbors, MessageModel.NeighborsMessage.Neighbors.Builder, MessageModel.NeighborsMessage.NeighborsOrBuilder>(
                  getNeighbors(),
                  getParentForChildren(),
                  isClean());
          neighbors_ = null;
        }
        return neighborsBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.NeighborsMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.NeighborsMessage)
    private static final MessageModel.NeighborsMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new MessageModel.NeighborsMessage();
    }

    public static MessageModel.NeighborsMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<NeighborsMessage>
        PARSER = new com.google.protobuf.AbstractParser<NeighborsMessage>() {
      @java.lang.Override
      public NeighborsMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new NeighborsMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<NeighborsMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<NeighborsMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public MessageModel.NeighborsMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface MessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.Message)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>.org.ethereum.protobuf.Message.DataType type = 1;</code>
     */
    int getTypeValue();
    /**
     * <code>.org.ethereum.protobuf.Message.DataType type = 1;</code>
     */
    MessageModel.Message.DataType getType();

    /**
     * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
     */
    boolean hasFromeAdd();
    /**
     * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
     */
    MessageModel.Message.FromeAdd getFromeAdd();
    /**
     * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
     */
    MessageModel.Message.FromeAddOrBuilder getFromeAddOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
     */
    boolean hasPingMessage();
    /**
     * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
     */
    MessageModel.PingMessage getPingMessage();
    /**
     * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
     */
    MessageModel.PingMessageOrBuilder getPingMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
     */
    boolean hasPongMessage();
    /**
     * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
     */
    MessageModel.PongMessage getPongMessage();
    /**
     * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
     */
    MessageModel.PongMessageOrBuilder getPongMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
     */
    boolean hasFindNodeMessage();
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
     */
    MessageModel.FindNodeMessage getFindNodeMessage();
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
     */
    MessageModel.FindNodeMessageOrBuilder getFindNodeMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
     */
    boolean hasNeighborsMessage();
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
     */
    MessageModel.NeighborsMessage getNeighborsMessage();
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
     */
    MessageModel.NeighborsMessageOrBuilder getNeighborsMessageOrBuilder();

    public MessageModel.Message.DataMsgCase getDataMsgCase();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.Message}
   */
  public  static final class Message extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.Message)
      MessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Message.newBuilder() to construct.
    private Message(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Message() {
      type_ = 0;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Message(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              int rawValue = input.readEnum();

              type_ = rawValue;
              break;
            }
            case 18: {
              MessageModel.Message.FromeAdd.Builder subBuilder = null;
              if (fromeAdd_ != null) {
                subBuilder = fromeAdd_.toBuilder();
              }
              fromeAdd_ = input.readMessage(MessageModel.Message.FromeAdd.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(fromeAdd_);
                fromeAdd_ = subBuilder.buildPartial();
              }

              break;
            }
            case 26: {
              MessageModel.PingMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 3) {
                subBuilder = ((MessageModel.PingMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(MessageModel.PingMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((MessageModel.PingMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 3;
              break;
            }
            case 34: {
              MessageModel.PongMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 4) {
                subBuilder = ((MessageModel.PongMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(MessageModel.PongMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((MessageModel.PongMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 4;
              break;
            }
            case 42: {
              MessageModel.FindNodeMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 5) {
                subBuilder = ((MessageModel.FindNodeMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(MessageModel.FindNodeMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((MessageModel.FindNodeMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 5;
              break;
            }
            case 50: {
              MessageModel.NeighborsMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 6) {
                subBuilder = ((MessageModel.NeighborsMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(MessageModel.NeighborsMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((MessageModel.NeighborsMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 6;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return MessageModel.internal_static_org_ethereum_protobuf_Message_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return MessageModel.internal_static_org_ethereum_protobuf_Message_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              MessageModel.Message.class, MessageModel.Message.Builder.class);
    }

    /**
     * Protobuf enum {@code org.ethereum.protobuf.Message.DataType}
     */
    public enum DataType
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>PingMessage = 0;</code>
       */
      PingMessage(0),
      /**
       * <code>PongMessage = 1;</code>
       */
      PongMessage(1),
      /**
       * <code>FindNodeMessage = 2;</code>
       */
      FindNodeMessage(2),
      /**
       * <code>NeighborsMessage = 3;</code>
       */
      NeighborsMessage(3),
      UNRECOGNIZED(-1),
      ;

      /**
       * <code>PingMessage = 0;</code>
       */
      public static final int PingMessage_VALUE = 0;
      /**
       * <code>PongMessage = 1;</code>
       */
      public static final int PongMessage_VALUE = 1;
      /**
       * <code>FindNodeMessage = 2;</code>
       */
      public static final int FindNodeMessage_VALUE = 2;
      /**
       * <code>NeighborsMessage = 3;</code>
       */
      public static final int NeighborsMessage_VALUE = 3;


      public final int getNumber() {
        if (this == UNRECOGNIZED) {
          throw new java.lang.IllegalArgumentException(
              "Can't get the number of an unknown enum value.");
        }
        return value;
      }

      /**
       * @deprecated Use {@link #forNumber(int)} instead.
       */
      @java.lang.Deprecated
      public static DataType valueOf(int value) {
        return forNumber(value);
      }

      public static DataType forNumber(int value) {
        switch (value) {
          case 0: return PingMessage;
          case 1: return PongMessage;
          case 2: return FindNodeMessage;
          case 3: return NeighborsMessage;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<DataType>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          DataType> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<DataType>() {
              public DataType findValueByNumber(int number) {
                return DataType.forNumber(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(ordinal());
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return MessageModel.Message.getDescriptor().getEnumTypes().get(0);
      }

      private static final DataType[] VALUES = values();

      public static DataType valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
          return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
      }

      private final int value;

      private DataType(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:org.ethereum.protobuf.Message.DataType)
    }

    public interface FromeAddOrBuilder extends
        // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.Message.FromeAdd)
        com.google.protobuf.MessageOrBuilder {

      /**
       * <code>bytes noteid = 1;</code>
       */
      com.google.protobuf.ByteString getNoteid();

      /**
       * <code>string ip = 2;</code>
       */
      java.lang.String getIp();
      /**
       * <code>string ip = 2;</code>
       */
      com.google.protobuf.ByteString
          getIpBytes();

      /**
       * <code>int32 prot = 3;</code>
       */
      int getProt();
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.Message.FromeAdd}
     */
    public  static final class FromeAdd extends
        com.google.protobuf.GeneratedMessageV3 implements
        // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.Message.FromeAdd)
        FromeAddOrBuilder {
    private static final long serialVersionUID = 0L;
      // Use FromeAdd.newBuilder() to construct.
      private FromeAdd(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
        super(builder);
      }
      private FromeAdd() {
        noteid_ = com.google.protobuf.ByteString.EMPTY;
        ip_ = "";
      }

      @java.lang.Override
      public final com.google.protobuf.UnknownFieldSet
      getUnknownFields() {
        return this.unknownFields;
      }
      private FromeAdd(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        this();
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        int mutable_bitField0_ = 0;
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
            com.google.protobuf.UnknownFieldSet.newBuilder();
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 10: {

                noteid_ = input.readBytes();
                break;
              }
              case 18: {
                java.lang.String s = input.readStringRequireUtf8();

                ip_ = s;
                break;
              }
              case 24: {

                prot_ = input.readInt32();
                break;
              }
              default: {
                if (!parseUnknownField(
                    input, unknownFields, extensionRegistry, tag)) {
                  done = true;
                }
                break;
              }
            }
          }
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(this);
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(
              e).setUnfinishedMessage(this);
        } finally {
          this.unknownFields = unknownFields.build();
          makeExtensionsImmutable();
        }
      }
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_Message_FromeAdd_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_Message_FromeAdd_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.Message.FromeAdd.class, MessageModel.Message.FromeAdd.Builder.class);
      }

      public static final int NOTEID_FIELD_NUMBER = 1;
      private com.google.protobuf.ByteString noteid_;
      /**
       * <code>bytes noteid = 1;</code>
       */
      public com.google.protobuf.ByteString getNoteid() {
        return noteid_;
      }

      public static final int IP_FIELD_NUMBER = 2;
      private volatile java.lang.Object ip_;
      /**
       * <code>string ip = 2;</code>
       */
      public java.lang.String getIp() {
        java.lang.Object ref = ip_;
        if (ref instanceof java.lang.String) {
          return (java.lang.String) ref;
        } else {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          ip_ = s;
          return s;
        }
      }
      /**
       * <code>string ip = 2;</code>
       */
      public com.google.protobuf.ByteString
          getIpBytes() {
        java.lang.Object ref = ip_;
        if (ref instanceof java.lang.String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          ip_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }

      public static final int PROT_FIELD_NUMBER = 3;
      private int prot_;
      /**
       * <code>int32 prot = 3;</code>
       */
      public int getProt() {
        return prot_;
      }

      private byte memoizedIsInitialized = -1;
      @java.lang.Override
      public final boolean isInitialized() {
        byte isInitialized = memoizedIsInitialized;
        if (isInitialized == 1) return true;
        if (isInitialized == 0) return false;

        memoizedIsInitialized = 1;
        return true;
      }

      @java.lang.Override
      public void writeTo(com.google.protobuf.CodedOutputStream output)
                          throws java.io.IOException {
        if (!noteid_.isEmpty()) {
          output.writeBytes(1, noteid_);
        }
        if (!getIpBytes().isEmpty()) {
          com.google.protobuf.GeneratedMessageV3.writeString(output, 2, ip_);
        }
        if (prot_ != 0) {
          output.writeInt32(3, prot_);
        }
        unknownFields.writeTo(output);
      }

      @java.lang.Override
      public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1) return size;

        size = 0;
        if (!noteid_.isEmpty()) {
          size += com.google.protobuf.CodedOutputStream
            .computeBytesSize(1, noteid_);
        }
        if (!getIpBytes().isEmpty()) {
          size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, ip_);
        }
        if (prot_ != 0) {
          size += com.google.protobuf.CodedOutputStream
            .computeInt32Size(3, prot_);
        }
        size += unknownFields.getSerializedSize();
        memoizedSize = size;
        return size;
      }

      @java.lang.Override
      public boolean equals(final java.lang.Object obj) {
        if (obj == this) {
         return true;
        }
        if (!(obj instanceof MessageModel.Message.FromeAdd)) {
          return super.equals(obj);
        }
        MessageModel.Message.FromeAdd other = (MessageModel.Message.FromeAdd) obj;

        if (!getNoteid()
            .equals(other.getNoteid())) return false;
        if (!getIp()
            .equals(other.getIp())) return false;
        if (getProt()
            != other.getProt()) return false;
        if (!unknownFields.equals(other.unknownFields)) return false;
        return true;
      }

      @java.lang.Override
      public int hashCode() {
        if (memoizedHashCode != 0) {
          return memoizedHashCode;
        }
        int hash = 41;
        hash = (19 * hash) + getDescriptor().hashCode();
        hash = (37 * hash) + NOTEID_FIELD_NUMBER;
        hash = (53 * hash) + getNoteid().hashCode();
        hash = (37 * hash) + IP_FIELD_NUMBER;
        hash = (53 * hash) + getIp().hashCode();
        hash = (37 * hash) + PROT_FIELD_NUMBER;
        hash = (53 * hash) + getProt();
        hash = (29 * hash) + unknownFields.hashCode();
        memoizedHashCode = hash;
        return hash;
      }

      public static MessageModel.Message.FromeAdd parseFrom(
          java.nio.ByteBuffer data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.Message.FromeAdd parseFrom(
          java.nio.ByteBuffer data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.Message.FromeAdd parseFrom(
          com.google.protobuf.ByteString data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.Message.FromeAdd parseFrom(
          com.google.protobuf.ByteString data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.Message.FromeAdd parseFrom(byte[] data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static MessageModel.Message.FromeAdd parseFrom(
          byte[] data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static MessageModel.Message.FromeAdd parseFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.Message.FromeAdd parseFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.Message.FromeAdd parseDelimitedFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input);
      }
      public static MessageModel.Message.FromeAdd parseDelimitedFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      public static MessageModel.Message.FromeAdd parseFrom(
          com.google.protobuf.CodedInputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static MessageModel.Message.FromeAdd parseFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }

      @java.lang.Override
      public Builder newBuilderForType() { return newBuilder(); }
      public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
      }
      public static Builder newBuilder(MessageModel.Message.FromeAdd prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }
      @java.lang.Override
      public Builder toBuilder() {
        return this == DEFAULT_INSTANCE
            ? new Builder() : new Builder().mergeFrom(this);
      }

      @java.lang.Override
      protected Builder newBuilderForType(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
      }
      /**
       * Protobuf type {@code org.ethereum.protobuf.Message.FromeAdd}
       */
      public static final class Builder extends
          com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
          // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.Message.FromeAdd)
          MessageModel.Message.FromeAddOrBuilder {
        public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
          return MessageModel.internal_static_org_ethereum_protobuf_Message_FromeAdd_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
          return MessageModel.internal_static_org_ethereum_protobuf_Message_FromeAdd_fieldAccessorTable
              .ensureFieldAccessorsInitialized(
                  MessageModel.Message.FromeAdd.class, MessageModel.Message.FromeAdd.Builder.class);
        }

        // Construct using MessageModel.Message.FromeAdd.newBuilder()
        private Builder() {
          maybeForceBuilderInitialization();
        }

        private Builder(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
          super(parent);
          maybeForceBuilderInitialization();
        }
        private void maybeForceBuilderInitialization() {
          if (com.google.protobuf.GeneratedMessageV3
                  .alwaysUseFieldBuilders) {
          }
        }
        @java.lang.Override
        public Builder clear() {
          super.clear();
          noteid_ = com.google.protobuf.ByteString.EMPTY;

          ip_ = "";

          prot_ = 0;

          return this;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
          return MessageModel.internal_static_org_ethereum_protobuf_Message_FromeAdd_descriptor;
        }

        @java.lang.Override
        public MessageModel.Message.FromeAdd getDefaultInstanceForType() {
          return MessageModel.Message.FromeAdd.getDefaultInstance();
        }

        @java.lang.Override
        public MessageModel.Message.FromeAdd build() {
          MessageModel.Message.FromeAdd result = buildPartial();
          if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
          }
          return result;
        }

        @java.lang.Override
        public MessageModel.Message.FromeAdd buildPartial() {
          MessageModel.Message.FromeAdd result = new MessageModel.Message.FromeAdd(this);
          result.noteid_ = noteid_;
          result.ip_ = ip_;
          result.prot_ = prot_;
          onBuilt();
          return result;
        }

        @java.lang.Override
        public Builder clone() {
          return super.clone();
        }
        @java.lang.Override
        public Builder setField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.setField(field, value);
        }
        @java.lang.Override
        public Builder clearField(
            com.google.protobuf.Descriptors.FieldDescriptor field) {
          return super.clearField(field);
        }
        @java.lang.Override
        public Builder clearOneof(
            com.google.protobuf.Descriptors.OneofDescriptor oneof) {
          return super.clearOneof(oneof);
        }
        @java.lang.Override
        public Builder setRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            int index, java.lang.Object value) {
          return super.setRepeatedField(field, index, value);
        }
        @java.lang.Override
        public Builder addRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
          return super.addRepeatedField(field, value);
        }
        @java.lang.Override
        public Builder mergeFrom(com.google.protobuf.Message other) {
          if (other instanceof MessageModel.Message.FromeAdd) {
            return mergeFrom((MessageModel.Message.FromeAdd)other);
          } else {
            super.mergeFrom(other);
            return this;
          }
        }

        public Builder mergeFrom(MessageModel.Message.FromeAdd other) {
          if (other == MessageModel.Message.FromeAdd.getDefaultInstance()) return this;
          if (other.getNoteid() != com.google.protobuf.ByteString.EMPTY) {
            setNoteid(other.getNoteid());
          }
          if (!other.getIp().isEmpty()) {
            ip_ = other.ip_;
            onChanged();
          }
          if (other.getProt() != 0) {
            setProt(other.getProt());
          }
          this.mergeUnknownFields(other.unknownFields);
          onChanged();
          return this;
        }

        @java.lang.Override
        public final boolean isInitialized() {
          return true;
        }

        @java.lang.Override
        public Builder mergeFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
          MessageModel.Message.FromeAdd parsedMessage = null;
          try {
            parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            parsedMessage = (MessageModel.Message.FromeAdd) e.getUnfinishedMessage();
            throw e.unwrapIOException();
          } finally {
            if (parsedMessage != null) {
              mergeFrom(parsedMessage);
            }
          }
          return this;
        }

        private com.google.protobuf.ByteString noteid_ = com.google.protobuf.ByteString.EMPTY;
        /**
         * <code>bytes noteid = 1;</code>
         */
        public com.google.protobuf.ByteString getNoteid() {
          return noteid_;
        }
        /**
         * <code>bytes noteid = 1;</code>
         */
        public Builder setNoteid(com.google.protobuf.ByteString value) {
          if (value == null) {
    throw new NullPointerException();
  }

          noteid_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>bytes noteid = 1;</code>
         */
        public Builder clearNoteid() {

          noteid_ = getDefaultInstance().getNoteid();
          onChanged();
          return this;
        }

        private java.lang.Object ip_ = "";
        /**
         * <code>string ip = 2;</code>
         */
        public java.lang.String getIp() {
          java.lang.Object ref = ip_;
          if (!(ref instanceof java.lang.String)) {
            com.google.protobuf.ByteString bs =
                (com.google.protobuf.ByteString) ref;
            java.lang.String s = bs.toStringUtf8();
            ip_ = s;
            return s;
          } else {
            return (java.lang.String) ref;
          }
        }
        /**
         * <code>string ip = 2;</code>
         */
        public com.google.protobuf.ByteString
            getIpBytes() {
          java.lang.Object ref = ip_;
          if (ref instanceof String) {
            com.google.protobuf.ByteString b =
                com.google.protobuf.ByteString.copyFromUtf8(
                    (java.lang.String) ref);
            ip_ = b;
            return b;
          } else {
            return (com.google.protobuf.ByteString) ref;
          }
        }
        /**
         * <code>string ip = 2;</code>
         */
        public Builder setIp(
            java.lang.String value) {
          if (value == null) {
    throw new NullPointerException();
  }

          ip_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>string ip = 2;</code>
         */
        public Builder clearIp() {

          ip_ = getDefaultInstance().getIp();
          onChanged();
          return this;
        }
        /**
         * <code>string ip = 2;</code>
         */
        public Builder setIpBytes(
            com.google.protobuf.ByteString value) {
          if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

          ip_ = value;
          onChanged();
          return this;
        }

        private int prot_ ;
        /**
         * <code>int32 prot = 3;</code>
         */
        public int getProt() {
          return prot_;
        }
        /**
         * <code>int32 prot = 3;</code>
         */
        public Builder setProt(int value) {

          prot_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>int32 prot = 3;</code>
         */
        public Builder clearProt() {

          prot_ = 0;
          onChanged();
          return this;
        }
        @java.lang.Override
        public final Builder setUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.setUnknownFields(unknownFields);
        }

        @java.lang.Override
        public final Builder mergeUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
          return super.mergeUnknownFields(unknownFields);
        }


        // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.Message.FromeAdd)
      }

      // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.Message.FromeAdd)
      private static final MessageModel.Message.FromeAdd DEFAULT_INSTANCE;
      static {
        DEFAULT_INSTANCE = new MessageModel.Message.FromeAdd();
      }

      public static MessageModel.Message.FromeAdd getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }

      private static final com.google.protobuf.Parser<FromeAdd>
          PARSER = new com.google.protobuf.AbstractParser<FromeAdd>() {
        @java.lang.Override
        public FromeAdd parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new FromeAdd(input, extensionRegistry);
        }
      };

      public static com.google.protobuf.Parser<FromeAdd> parser() {
        return PARSER;
      }

      @java.lang.Override
      public com.google.protobuf.Parser<FromeAdd> getParserForType() {
        return PARSER;
      }

      @java.lang.Override
      public MessageModel.Message.FromeAdd getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }

    }

    private int dataMsgCase_ = 0;
    private java.lang.Object dataMsg_;
    public enum DataMsgCase
        implements com.google.protobuf.Internal.EnumLite {
      PINGMESSAGE(3),
      PONGMESSAGE(4),
      FINDNODEMESSAGE(5),
      NEIGHBORSMESSAGE(6),
      DATAMSG_NOT_SET(0);
      private final int value;
      private DataMsgCase(int value) {
        this.value = value;
      }
      /**
       * @deprecated Use {@link #forNumber(int)} instead.
       */
      @java.lang.Deprecated
      public static DataMsgCase valueOf(int value) {
        return forNumber(value);
      }

      public static DataMsgCase forNumber(int value) {
        switch (value) {
          case 3: return PINGMESSAGE;
          case 4: return PONGMESSAGE;
          case 5: return FINDNODEMESSAGE;
          case 6: return NEIGHBORSMESSAGE;
          case 0: return DATAMSG_NOT_SET;
          default: return null;
        }
      }
      public int getNumber() {
        return this.value;
      }
    };

    public DataMsgCase
    getDataMsgCase() {
      return DataMsgCase.forNumber(
          dataMsgCase_);
    }

    public static final int TYPE_FIELD_NUMBER = 1;
    private int type_;
    /**
     * <code>.org.ethereum.protobuf.Message.DataType type = 1;</code>
     */
    public int getTypeValue() {
      return type_;
    }
    /**
     * <code>.org.ethereum.protobuf.Message.DataType type = 1;</code>
     */
    public MessageModel.Message.DataType getType() {
      @SuppressWarnings("deprecation")
      MessageModel.Message.DataType result = MessageModel.Message.DataType.valueOf(type_);
      return result == null ? MessageModel.Message.DataType.UNRECOGNIZED : result;
    }

    public static final int FROMEADD_FIELD_NUMBER = 2;
    private MessageModel.Message.FromeAdd fromeAdd_;
    /**
     * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
     */
    public boolean hasFromeAdd() {
      return fromeAdd_ != null;
    }
    /**
     * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
     */
    public MessageModel.Message.FromeAdd getFromeAdd() {
      return fromeAdd_ == null ? MessageModel.Message.FromeAdd.getDefaultInstance() : fromeAdd_;
    }
    /**
     * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
     */
    public MessageModel.Message.FromeAddOrBuilder getFromeAddOrBuilder() {
      return getFromeAdd();
    }

    public static final int PINGMESSAGE_FIELD_NUMBER = 3;
    /**
     * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
     */
    public boolean hasPingMessage() {
      return dataMsgCase_ == 3;
    }
    /**
     * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
     */
    public MessageModel.PingMessage getPingMessage() {
      if (dataMsgCase_ == 3) {
         return (MessageModel.PingMessage) dataMsg_;
      }
      return MessageModel.PingMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
     */
    public MessageModel.PingMessageOrBuilder getPingMessageOrBuilder() {
      if (dataMsgCase_ == 3) {
         return (MessageModel.PingMessage) dataMsg_;
      }
      return MessageModel.PingMessage.getDefaultInstance();
    }

    public static final int PONGMESSAGE_FIELD_NUMBER = 4;
    /**
     * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
     */
    public boolean hasPongMessage() {
      return dataMsgCase_ == 4;
    }
    /**
     * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
     */
    public MessageModel.PongMessage getPongMessage() {
      if (dataMsgCase_ == 4) {
         return (MessageModel.PongMessage) dataMsg_;
      }
      return MessageModel.PongMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
     */
    public MessageModel.PongMessageOrBuilder getPongMessageOrBuilder() {
      if (dataMsgCase_ == 4) {
         return (MessageModel.PongMessage) dataMsg_;
      }
      return MessageModel.PongMessage.getDefaultInstance();
    }

    public static final int FINDNODEMESSAGE_FIELD_NUMBER = 5;
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
     */
    public boolean hasFindNodeMessage() {
      return dataMsgCase_ == 5;
    }
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
     */
    public MessageModel.FindNodeMessage getFindNodeMessage() {
      if (dataMsgCase_ == 5) {
         return (MessageModel.FindNodeMessage) dataMsg_;
      }
      return MessageModel.FindNodeMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
     */
    public MessageModel.FindNodeMessageOrBuilder getFindNodeMessageOrBuilder() {
      if (dataMsgCase_ == 5) {
         return (MessageModel.FindNodeMessage) dataMsg_;
      }
      return MessageModel.FindNodeMessage.getDefaultInstance();
    }

    public static final int NEIGHBORSMESSAGE_FIELD_NUMBER = 6;
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
     */
    public boolean hasNeighborsMessage() {
      return dataMsgCase_ == 6;
    }
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
     */
    public MessageModel.NeighborsMessage getNeighborsMessage() {
      if (dataMsgCase_ == 6) {
         return (MessageModel.NeighborsMessage) dataMsg_;
      }
      return MessageModel.NeighborsMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
     */
    public MessageModel.NeighborsMessageOrBuilder getNeighborsMessageOrBuilder() {
      if (dataMsgCase_ == 6) {
         return (MessageModel.NeighborsMessage) dataMsg_;
      }
      return MessageModel.NeighborsMessage.getDefaultInstance();
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (type_ != MessageModel.Message.DataType.PingMessage.getNumber()) {
        output.writeEnum(1, type_);
      }
      if (fromeAdd_ != null) {
        output.writeMessage(2, getFromeAdd());
      }
      if (dataMsgCase_ == 3) {
        output.writeMessage(3, (MessageModel.PingMessage) dataMsg_);
      }
      if (dataMsgCase_ == 4) {
        output.writeMessage(4, (MessageModel.PongMessage) dataMsg_);
      }
      if (dataMsgCase_ == 5) {
        output.writeMessage(5, (MessageModel.FindNodeMessage) dataMsg_);
      }
      if (dataMsgCase_ == 6) {
        output.writeMessage(6, (MessageModel.NeighborsMessage) dataMsg_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (type_ != MessageModel.Message.DataType.PingMessage.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, type_);
      }
      if (fromeAdd_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getFromeAdd());
      }
      if (dataMsgCase_ == 3) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, (MessageModel.PingMessage) dataMsg_);
      }
      if (dataMsgCase_ == 4) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, (MessageModel.PongMessage) dataMsg_);
      }
      if (dataMsgCase_ == 5) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(5, (MessageModel.FindNodeMessage) dataMsg_);
      }
      if (dataMsgCase_ == 6) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(6, (MessageModel.NeighborsMessage) dataMsg_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof MessageModel.Message)) {
        return super.equals(obj);
      }
      MessageModel.Message other = (MessageModel.Message) obj;

      if (type_ != other.type_) return false;
      if (hasFromeAdd() != other.hasFromeAdd()) return false;
      if (hasFromeAdd()) {
        if (!getFromeAdd()
            .equals(other.getFromeAdd())) return false;
      }
      if (!getDataMsgCase().equals(other.getDataMsgCase())) return false;
      switch (dataMsgCase_) {
        case 3:
          if (!getPingMessage()
              .equals(other.getPingMessage())) return false;
          break;
        case 4:
          if (!getPongMessage()
              .equals(other.getPongMessage())) return false;
          break;
        case 5:
          if (!getFindNodeMessage()
              .equals(other.getFindNodeMessage())) return false;
          break;
        case 6:
          if (!getNeighborsMessage()
              .equals(other.getNeighborsMessage())) return false;
          break;
        case 0:
        default:
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + type_;
      if (hasFromeAdd()) {
        hash = (37 * hash) + FROMEADD_FIELD_NUMBER;
        hash = (53 * hash) + getFromeAdd().hashCode();
      }
      switch (dataMsgCase_) {
        case 3:
          hash = (37 * hash) + PINGMESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getPingMessage().hashCode();
          break;
        case 4:
          hash = (37 * hash) + PONGMESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getPongMessage().hashCode();
          break;
        case 5:
          hash = (37 * hash) + FINDNODEMESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getFindNodeMessage().hashCode();
          break;
        case 6:
          hash = (37 * hash) + NEIGHBORSMESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getNeighborsMessage().hashCode();
          break;
        case 0:
        default:
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static MessageModel.Message parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.Message parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.Message parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.Message parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.Message parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageModel.Message parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageModel.Message parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.Message parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.Message parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static MessageModel.Message parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageModel.Message parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageModel.Message parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(MessageModel.Message prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.ethereum.protobuf.Message}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.Message)
        MessageModel.MessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageModel.internal_static_org_ethereum_protobuf_Message_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageModel.internal_static_org_ethereum_protobuf_Message_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageModel.Message.class, MessageModel.Message.Builder.class);
      }

      // Construct using MessageModel.Message.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        type_ = 0;

        if (fromeAddBuilder_ == null) {
          fromeAdd_ = null;
        } else {
          fromeAdd_ = null;
          fromeAddBuilder_ = null;
        }
        dataMsgCase_ = 0;
        dataMsg_ = null;
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return MessageModel.internal_static_org_ethereum_protobuf_Message_descriptor;
      }

      @java.lang.Override
      public MessageModel.Message getDefaultInstanceForType() {
        return MessageModel.Message.getDefaultInstance();
      }

      @java.lang.Override
      public MessageModel.Message build() {
        MessageModel.Message result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public MessageModel.Message buildPartial() {
        MessageModel.Message result = new MessageModel.Message(this);
        result.type_ = type_;
        if (fromeAddBuilder_ == null) {
          result.fromeAdd_ = fromeAdd_;
        } else {
          result.fromeAdd_ = fromeAddBuilder_.build();
        }
        if (dataMsgCase_ == 3) {
          if (pingMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = pingMessageBuilder_.build();
          }
        }
        if (dataMsgCase_ == 4) {
          if (pongMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = pongMessageBuilder_.build();
          }
        }
        if (dataMsgCase_ == 5) {
          if (findNodeMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = findNodeMessageBuilder_.build();
          }
        }
        if (dataMsgCase_ == 6) {
          if (neighborsMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = neighborsMessageBuilder_.build();
          }
        }
        result.dataMsgCase_ = dataMsgCase_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof MessageModel.Message) {
          return mergeFrom((MessageModel.Message)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(MessageModel.Message other) {
        if (other == MessageModel.Message.getDefaultInstance()) return this;
        if (other.type_ != 0) {
          setTypeValue(other.getTypeValue());
        }
        if (other.hasFromeAdd()) {
          mergeFromeAdd(other.getFromeAdd());
        }
        switch (other.getDataMsgCase()) {
          case PINGMESSAGE: {
            mergePingMessage(other.getPingMessage());
            break;
          }
          case PONGMESSAGE: {
            mergePongMessage(other.getPongMessage());
            break;
          }
          case FINDNODEMESSAGE: {
            mergeFindNodeMessage(other.getFindNodeMessage());
            break;
          }
          case NEIGHBORSMESSAGE: {
            mergeNeighborsMessage(other.getNeighborsMessage());
            break;
          }
          case DATAMSG_NOT_SET: {
            break;
          }
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        MessageModel.Message parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (MessageModel.Message) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int dataMsgCase_ = 0;
      private java.lang.Object dataMsg_;
      public DataMsgCase
          getDataMsgCase() {
        return DataMsgCase.forNumber(
            dataMsgCase_);
      }

      public Builder clearDataMsg() {
        dataMsgCase_ = 0;
        dataMsg_ = null;
        onChanged();
        return this;
      }


      private int type_ = 0;
      /**
       * <code>.org.ethereum.protobuf.Message.DataType type = 1;</code>
       */
      public int getTypeValue() {
        return type_;
      }
      /**
       * <code>.org.ethereum.protobuf.Message.DataType type = 1;</code>
       */
      public Builder setTypeValue(int value) {
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.Message.DataType type = 1;</code>
       */
      public MessageModel.Message.DataType getType() {
        @SuppressWarnings("deprecation")
        MessageModel.Message.DataType result = MessageModel.Message.DataType.valueOf(type_);
        return result == null ? MessageModel.Message.DataType.UNRECOGNIZED : result;
      }
      /**
       * <code>.org.ethereum.protobuf.Message.DataType type = 1;</code>
       */
      public Builder setType(MessageModel.Message.DataType value) {
        if (value == null) {
          throw new NullPointerException();
        }

        type_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.Message.DataType type = 1;</code>
       */
      public Builder clearType() {

        type_ = 0;
        onChanged();
        return this;
      }

      private MessageModel.Message.FromeAdd fromeAdd_;
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.Message.FromeAdd, MessageModel.Message.FromeAdd.Builder, MessageModel.Message.FromeAddOrBuilder> fromeAddBuilder_;
      /**
       * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
       */
      public boolean hasFromeAdd() {
        return fromeAddBuilder_ != null || fromeAdd_ != null;
      }
      /**
       * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
       */
      public MessageModel.Message.FromeAdd getFromeAdd() {
        if (fromeAddBuilder_ == null) {
          return fromeAdd_ == null ? MessageModel.Message.FromeAdd.getDefaultInstance() : fromeAdd_;
        } else {
          return fromeAddBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
       */
      public Builder setFromeAdd(MessageModel.Message.FromeAdd value) {
        if (fromeAddBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          fromeAdd_ = value;
          onChanged();
        } else {
          fromeAddBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
       */
      public Builder setFromeAdd(
          MessageModel.Message.FromeAdd.Builder builderForValue) {
        if (fromeAddBuilder_ == null) {
          fromeAdd_ = builderForValue.build();
          onChanged();
        } else {
          fromeAddBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
       */
      public Builder mergeFromeAdd(MessageModel.Message.FromeAdd value) {
        if (fromeAddBuilder_ == null) {
          if (fromeAdd_ != null) {
            fromeAdd_ =
              MessageModel.Message.FromeAdd.newBuilder(fromeAdd_).mergeFrom(value).buildPartial();
          } else {
            fromeAdd_ = value;
          }
          onChanged();
        } else {
          fromeAddBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
       */
      public Builder clearFromeAdd() {
        if (fromeAddBuilder_ == null) {
          fromeAdd_ = null;
          onChanged();
        } else {
          fromeAdd_ = null;
          fromeAddBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
       */
      public MessageModel.Message.FromeAdd.Builder getFromeAddBuilder() {

        onChanged();
        return getFromeAddFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
       */
      public MessageModel.Message.FromeAddOrBuilder getFromeAddOrBuilder() {
        if (fromeAddBuilder_ != null) {
          return fromeAddBuilder_.getMessageOrBuilder();
        } else {
          return fromeAdd_ == null ?
              MessageModel.Message.FromeAdd.getDefaultInstance() : fromeAdd_;
        }
      }
      /**
       * <code>.org.ethereum.protobuf.Message.FromeAdd fromeAdd = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.Message.FromeAdd, MessageModel.Message.FromeAdd.Builder, MessageModel.Message.FromeAddOrBuilder>
          getFromeAddFieldBuilder() {
        if (fromeAddBuilder_ == null) {
          fromeAddBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              MessageModel.Message.FromeAdd, MessageModel.Message.FromeAdd.Builder, MessageModel.Message.FromeAddOrBuilder>(
                  getFromeAdd(),
                  getParentForChildren(),
                  isClean());
          fromeAdd_ = null;
        }
        return fromeAddBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.PingMessage, MessageModel.PingMessage.Builder, MessageModel.PingMessageOrBuilder> pingMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
       */
      public boolean hasPingMessage() {
        return dataMsgCase_ == 3;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
       */
      public MessageModel.PingMessage getPingMessage() {
        if (pingMessageBuilder_ == null) {
          if (dataMsgCase_ == 3) {
            return (MessageModel.PingMessage) dataMsg_;
          }
          return MessageModel.PingMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 3) {
            return pingMessageBuilder_.getMessage();
          }
          return MessageModel.PingMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
       */
      public Builder setPingMessage(MessageModel.PingMessage value) {
        if (pingMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          pingMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 3;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
       */
      public Builder setPingMessage(
          MessageModel.PingMessage.Builder builderForValue) {
        if (pingMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          pingMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 3;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
       */
      public Builder mergePingMessage(MessageModel.PingMessage value) {
        if (pingMessageBuilder_ == null) {
          if (dataMsgCase_ == 3 &&
              dataMsg_ != MessageModel.PingMessage.getDefaultInstance()) {
            dataMsg_ = MessageModel.PingMessage.newBuilder((MessageModel.PingMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 3) {
            pingMessageBuilder_.mergeFrom(value);
          }
          pingMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 3;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
       */
      public Builder clearPingMessage() {
        if (pingMessageBuilder_ == null) {
          if (dataMsgCase_ == 3) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
            onChanged();
          }
        } else {
          if (dataMsgCase_ == 3) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
          }
          pingMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
       */
      public MessageModel.PingMessage.Builder getPingMessageBuilder() {
        return getPingMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
       */
      public MessageModel.PingMessageOrBuilder getPingMessageOrBuilder() {
        if ((dataMsgCase_ == 3) && (pingMessageBuilder_ != null)) {
          return pingMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 3) {
            return (MessageModel.PingMessage) dataMsg_;
          }
          return MessageModel.PingMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.PingMessage pingMessage = 3;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.PingMessage, MessageModel.PingMessage.Builder, MessageModel.PingMessageOrBuilder>
          getPingMessageFieldBuilder() {
        if (pingMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 3)) {
            dataMsg_ = MessageModel.PingMessage.getDefaultInstance();
          }
          pingMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              MessageModel.PingMessage, MessageModel.PingMessage.Builder, MessageModel.PingMessageOrBuilder>(
                  (MessageModel.PingMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 3;
        onChanged();;
        return pingMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.PongMessage, MessageModel.PongMessage.Builder, MessageModel.PongMessageOrBuilder> pongMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
       */
      public boolean hasPongMessage() {
        return dataMsgCase_ == 4;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
       */
      public MessageModel.PongMessage getPongMessage() {
        if (pongMessageBuilder_ == null) {
          if (dataMsgCase_ == 4) {
            return (MessageModel.PongMessage) dataMsg_;
          }
          return MessageModel.PongMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 4) {
            return pongMessageBuilder_.getMessage();
          }
          return MessageModel.PongMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
       */
      public Builder setPongMessage(MessageModel.PongMessage value) {
        if (pongMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          pongMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 4;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
       */
      public Builder setPongMessage(
          MessageModel.PongMessage.Builder builderForValue) {
        if (pongMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          pongMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 4;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
       */
      public Builder mergePongMessage(MessageModel.PongMessage value) {
        if (pongMessageBuilder_ == null) {
          if (dataMsgCase_ == 4 &&
              dataMsg_ != MessageModel.PongMessage.getDefaultInstance()) {
            dataMsg_ = MessageModel.PongMessage.newBuilder((MessageModel.PongMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 4) {
            pongMessageBuilder_.mergeFrom(value);
          }
          pongMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 4;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
       */
      public Builder clearPongMessage() {
        if (pongMessageBuilder_ == null) {
          if (dataMsgCase_ == 4) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
            onChanged();
          }
        } else {
          if (dataMsgCase_ == 4) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
          }
          pongMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
       */
      public MessageModel.PongMessage.Builder getPongMessageBuilder() {
        return getPongMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
       */
      public MessageModel.PongMessageOrBuilder getPongMessageOrBuilder() {
        if ((dataMsgCase_ == 4) && (pongMessageBuilder_ != null)) {
          return pongMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 4) {
            return (MessageModel.PongMessage) dataMsg_;
          }
          return MessageModel.PongMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.PongMessage pongMessage = 4;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.PongMessage, MessageModel.PongMessage.Builder, MessageModel.PongMessageOrBuilder>
          getPongMessageFieldBuilder() {
        if (pongMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 4)) {
            dataMsg_ = MessageModel.PongMessage.getDefaultInstance();
          }
          pongMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              MessageModel.PongMessage, MessageModel.PongMessage.Builder, MessageModel.PongMessageOrBuilder>(
                  (MessageModel.PongMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 4;
        onChanged();;
        return pongMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.FindNodeMessage, MessageModel.FindNodeMessage.Builder, MessageModel.FindNodeMessageOrBuilder> findNodeMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
       */
      public boolean hasFindNodeMessage() {
        return dataMsgCase_ == 5;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
       */
      public MessageModel.FindNodeMessage getFindNodeMessage() {
        if (findNodeMessageBuilder_ == null) {
          if (dataMsgCase_ == 5) {
            return (MessageModel.FindNodeMessage) dataMsg_;
          }
          return MessageModel.FindNodeMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 5) {
            return findNodeMessageBuilder_.getMessage();
          }
          return MessageModel.FindNodeMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
       */
      public Builder setFindNodeMessage(MessageModel.FindNodeMessage value) {
        if (findNodeMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          findNodeMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 5;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
       */
      public Builder setFindNodeMessage(
          MessageModel.FindNodeMessage.Builder builderForValue) {
        if (findNodeMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          findNodeMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 5;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
       */
      public Builder mergeFindNodeMessage(MessageModel.FindNodeMessage value) {
        if (findNodeMessageBuilder_ == null) {
          if (dataMsgCase_ == 5 &&
              dataMsg_ != MessageModel.FindNodeMessage.getDefaultInstance()) {
            dataMsg_ = MessageModel.FindNodeMessage.newBuilder((MessageModel.FindNodeMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 5) {
            findNodeMessageBuilder_.mergeFrom(value);
          }
          findNodeMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 5;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
       */
      public Builder clearFindNodeMessage() {
        if (findNodeMessageBuilder_ == null) {
          if (dataMsgCase_ == 5) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
            onChanged();
          }
        } else {
          if (dataMsgCase_ == 5) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
          }
          findNodeMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
       */
      public MessageModel.FindNodeMessage.Builder getFindNodeMessageBuilder() {
        return getFindNodeMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
       */
      public MessageModel.FindNodeMessageOrBuilder getFindNodeMessageOrBuilder() {
        if ((dataMsgCase_ == 5) && (findNodeMessageBuilder_ != null)) {
          return findNodeMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 5) {
            return (MessageModel.FindNodeMessage) dataMsg_;
          }
          return MessageModel.FindNodeMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.FindNodeMessage findNodeMessage = 5;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.FindNodeMessage, MessageModel.FindNodeMessage.Builder, MessageModel.FindNodeMessageOrBuilder>
          getFindNodeMessageFieldBuilder() {
        if (findNodeMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 5)) {
            dataMsg_ = MessageModel.FindNodeMessage.getDefaultInstance();
          }
          findNodeMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              MessageModel.FindNodeMessage, MessageModel.FindNodeMessage.Builder, MessageModel.FindNodeMessageOrBuilder>(
                  (MessageModel.FindNodeMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 5;
        onChanged();;
        return findNodeMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.NeighborsMessage, MessageModel.NeighborsMessage.Builder, MessageModel.NeighborsMessageOrBuilder> neighborsMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
       */
      public boolean hasNeighborsMessage() {
        return dataMsgCase_ == 6;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
       */
      public MessageModel.NeighborsMessage getNeighborsMessage() {
        if (neighborsMessageBuilder_ == null) {
          if (dataMsgCase_ == 6) {
            return (MessageModel.NeighborsMessage) dataMsg_;
          }
          return MessageModel.NeighborsMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 6) {
            return neighborsMessageBuilder_.getMessage();
          }
          return MessageModel.NeighborsMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
       */
      public Builder setNeighborsMessage(MessageModel.NeighborsMessage value) {
        if (neighborsMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          neighborsMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 6;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
       */
      public Builder setNeighborsMessage(
          MessageModel.NeighborsMessage.Builder builderForValue) {
        if (neighborsMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          neighborsMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 6;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
       */
      public Builder mergeNeighborsMessage(MessageModel.NeighborsMessage value) {
        if (neighborsMessageBuilder_ == null) {
          if (dataMsgCase_ == 6 &&
              dataMsg_ != MessageModel.NeighborsMessage.getDefaultInstance()) {
            dataMsg_ = MessageModel.NeighborsMessage.newBuilder((MessageModel.NeighborsMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 6) {
            neighborsMessageBuilder_.mergeFrom(value);
          }
          neighborsMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 6;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
       */
      public Builder clearNeighborsMessage() {
        if (neighborsMessageBuilder_ == null) {
          if (dataMsgCase_ == 6) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
            onChanged();
          }
        } else {
          if (dataMsgCase_ == 6) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
          }
          neighborsMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
       */
      public MessageModel.NeighborsMessage.Builder getNeighborsMessageBuilder() {
        return getNeighborsMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
       */
      public MessageModel.NeighborsMessageOrBuilder getNeighborsMessageOrBuilder() {
        if ((dataMsgCase_ == 6) && (neighborsMessageBuilder_ != null)) {
          return neighborsMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 6) {
            return (MessageModel.NeighborsMessage) dataMsg_;
          }
          return MessageModel.NeighborsMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.NeighborsMessage neighborsMessage = 6;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          MessageModel.NeighborsMessage, MessageModel.NeighborsMessage.Builder, MessageModel.NeighborsMessageOrBuilder>
          getNeighborsMessageFieldBuilder() {
        if (neighborsMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 6)) {
            dataMsg_ = MessageModel.NeighborsMessage.getDefaultInstance();
          }
          neighborsMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              MessageModel.NeighborsMessage, MessageModel.NeighborsMessage.Builder, MessageModel.NeighborsMessageOrBuilder>(
                  (MessageModel.NeighborsMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 6;
        onChanged();;
        return neighborsMessageBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.Message)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.Message)
    private static final MessageModel.Message DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new MessageModel.Message();
    }

    public static MessageModel.Message getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Message>
        PARSER = new com.google.protobuf.AbstractParser<Message>() {
      @java.lang.Override
      public Message parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Message(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Message> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Message> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public MessageModel.Message getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_PongMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_PongMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_PongMessage_Pong_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_PongMessage_Pong_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_PingMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_PingMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_PingMessage_Ping_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_PingMessage_Ping_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_FindNodeMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_FindNodeMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_NeighborsMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_NeighborsMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_Message_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_Message_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_Message_FromeAdd_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_Message_FromeAdd_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rMessage.proto\022\025org.ethereum.protobuf\"\235" +
      "\001\n\013PongMessage\022\r\n\005magic\030\001 \001(\t\022\016\n\006length\030" +
      "\002 \001(\017\022\020\n\010checksum\030\003 \001(\014\0225\n\004pong\030\004 \001(\0132\'." +
      "org.ethereum.protobuf.PongMessage.Pong\032&" +
      "\n\004Pong\022\r\n\005token\030\001 \001(\014\022\017\n\007expires\030\002 \001(\004\"\311" +
      "\002\n\013PingMessage\022\r\n\005magic\030\001 \001(\t\022\016\n\006length\030" +
      "\002 \001(\017\022\020\n\010checksum\030\003 \001(\014\0225\n\004ping\030\004 \001(\0132\'." +
      "org.ethereum.protobuf.PingMessage.Ping\032\321" +
      "\001\n\004Ping\022?\n\006toHost\030\001 \001(\0132/.org.ethereum.p" +
      "rotobuf.PingMessage.Ping.Address\022A\n\010from" +
      "Host\030\002 \001(\0132/.org.ethereum.protobuf.PingM" +
      "essage.Ping.Address\022\017\n\007expires\030\003 \001(\004\022\017\n\007" +
      "version\030\004 \001(\005\032#\n\007Address\022\n\n\002ip\030\001 \001(\t\022\014\n\004" +
      "prot\030\002 \001(\005\"\262\001\n\017FindNodeMessage\022\r\n\005magic\030" +
      "\001 \001(\t\022\016\n\006length\030\002 \001(\017\022\020\n\010checksum\030\003 \001(\014\022" +
      "A\n\010findNode\030\004 \001(\0132/.org.ethereum.protobu" +
      "f.FindNodeMessage.FindNode\032+\n\010FindNode\022\016" +
      "\n\006target\030\001 \001(\014\022\017\n\007expires\030\002 \001(\004\"\240\002\n\020Neig" +
      "hborsMessage\022\r\n\005magic\030\001 \001(\t\022\016\n\006length\030\002 " +
      "\001(\017\022\020\n\010checksum\030\003 \001(\014\022D\n\tneighbors\030\004 \001(\013" +
      "21.org.ethereum.protobuf.NeighborsMessag" +
      "e.Neighbors\032\224\001\n\tNeighbors\022D\n\004node\030\001 \003(\0132" +
      "6.org.ethereum.protobuf.NeighborsMessage" +
      ".Neighbors.Node\022\017\n\007expires\030\002 \001(\004\0320\n\004Node" +
      "\022\016\n\006noteid\030\001 \001(\014\022\n\n\002ip\030\002 \001(\t\022\014\n\004prot\030\003 \001" +
      "(\005\"\223\004\n\007Message\0225\n\004type\030\001 \001(\0162\'.org.ether" +
      "eum.protobuf.Message.DataType\0229\n\010fromeAd" +
      "d\030\002 \001(\0132\'.org.ethereum.protobuf.Message." +
      "FromeAdd\0229\n\013pingMessage\030\003 \001(\0132\".org.ethe" +
      "reum.protobuf.PingMessageH\000\0229\n\013pongMessa" +
      "ge\030\004 \001(\0132\".org.ethereum.protobuf.PongMes" +
      "sageH\000\022A\n\017findNodeMessage\030\005 \001(\0132&.org.et" +
      "hereum.protobuf.FindNodeMessageH\000\022C\n\020nei" +
      "ghborsMessage\030\006 \001(\0132\'.org.ethereum.proto" +
      "buf.NeighborsMessageH\000\0324\n\010FromeAdd\022\016\n\006no" +
      "teid\030\001 \001(\014\022\n\n\002ip\030\002 \001(\t\022\014\n\004prot\030\003 \001(\005\"W\n\010" +
      "DataType\022\017\n\013PingMessage\020\000\022\017\n\013PongMessage" +
      "\020\001\022\023\n\017FindNodeMessage\020\002\022\024\n\020NeighborsMess" +
      "age\020\003B\t\n\007dataMsgB\020B\014MessageModelH\001b\006prot" +
      "o3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_org_ethereum_protobuf_PongMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_ethereum_protobuf_PongMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_PongMessage_descriptor,
        new java.lang.String[] { "Magic", "Length", "Checksum", "Pong", });
    internal_static_org_ethereum_protobuf_PongMessage_Pong_descriptor =
      internal_static_org_ethereum_protobuf_PongMessage_descriptor.getNestedTypes().get(0);
    internal_static_org_ethereum_protobuf_PongMessage_Pong_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_PongMessage_Pong_descriptor,
        new java.lang.String[] { "Token", "Expires", });
    internal_static_org_ethereum_protobuf_PingMessage_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_org_ethereum_protobuf_PingMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_PingMessage_descriptor,
        new java.lang.String[] { "Magic", "Length", "Checksum", "Ping", });
    internal_static_org_ethereum_protobuf_PingMessage_Ping_descriptor =
      internal_static_org_ethereum_protobuf_PingMessage_descriptor.getNestedTypes().get(0);
    internal_static_org_ethereum_protobuf_PingMessage_Ping_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_PingMessage_Ping_descriptor,
        new java.lang.String[] { "ToHost", "FromHost", "Expires", "Version", });
    internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_descriptor =
      internal_static_org_ethereum_protobuf_PingMessage_Ping_descriptor.getNestedTypes().get(0);
    internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_PingMessage_Ping_Address_descriptor,
        new java.lang.String[] { "Ip", "Prot", });
    internal_static_org_ethereum_protobuf_FindNodeMessage_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_org_ethereum_protobuf_FindNodeMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_FindNodeMessage_descriptor,
        new java.lang.String[] { "Magic", "Length", "Checksum", "FindNode", });
    internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_descriptor =
      internal_static_org_ethereum_protobuf_FindNodeMessage_descriptor.getNestedTypes().get(0);
    internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_FindNodeMessage_FindNode_descriptor,
        new java.lang.String[] { "TargetState", "Expires", });
    internal_static_org_ethereum_protobuf_NeighborsMessage_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_org_ethereum_protobuf_NeighborsMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_NeighborsMessage_descriptor,
        new java.lang.String[] { "Magic", "Length", "Checksum", "Neighbors", });
    internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_descriptor =
      internal_static_org_ethereum_protobuf_NeighborsMessage_descriptor.getNestedTypes().get(0);
    internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_descriptor,
        new java.lang.String[] { "Node", "Expires", });
    internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_descriptor =
      internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_descriptor.getNestedTypes().get(0);
    internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_NeighborsMessage_Neighbors_Node_descriptor,
        new java.lang.String[] { "Noteid", "Ip", "Prot", });
    internal_static_org_ethereum_protobuf_Message_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_org_ethereum_protobuf_Message_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_Message_descriptor,
        new java.lang.String[] { "Type", "FromeAdd", "PingMessage", "PongMessage", "FindNodeMessage", "NeighborsMessage", "DataMsg", });
    internal_static_org_ethereum_protobuf_Message_FromeAdd_descriptor =
      internal_static_org_ethereum_protobuf_Message_descriptor.getNestedTypes().get(0);
    internal_static_org_ethereum_protobuf_Message_FromeAdd_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_Message_FromeAdd_descriptor,
        new java.lang.String[] { "Noteid", "Ip", "Prot", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}