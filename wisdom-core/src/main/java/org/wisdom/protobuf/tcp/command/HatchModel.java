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

package org.wisdom.protobuf.tcp.command;

public final class HatchModel {
  private HatchModel() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface HatchOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.command.Hatch)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>bytes info = 1;</code>
     */
    com.google.protobuf.ByteString getInfo();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.command.Hatch}
   */
  public  static final class Hatch extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.command.Hatch)
      HatchOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Hatch.newBuilder() to construct.
    private Hatch(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Hatch() {
      info_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Hatch(
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

              info_ = input.readBytes();
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
      return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Hatch_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Hatch_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              HatchModel.Hatch.class, HatchModel.Hatch.Builder.class);
    }

    public static final int INFO_FIELD_NUMBER = 1;
    private com.google.protobuf.ByteString info_;
    /**
     * <code>bytes info = 1;</code>
     */
    public com.google.protobuf.ByteString getInfo() {
      return info_;
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
      if (!info_.isEmpty()) {
        output.writeBytes(1, info_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!info_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, info_);
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
      if (!(obj instanceof HatchModel.Hatch)) {
        return super.equals(obj);
      }
      HatchModel.Hatch other = (HatchModel.Hatch) obj;

      if (!getInfo()
          .equals(other.getInfo())) return false;
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
      hash = (37 * hash) + INFO_FIELD_NUMBER;
      hash = (53 * hash) + getInfo().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static HatchModel.Hatch parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static HatchModel.Hatch parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static HatchModel.Hatch parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static HatchModel.Hatch parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static HatchModel.Hatch parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static HatchModel.Hatch parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static HatchModel.Hatch parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static HatchModel.Hatch parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static HatchModel.Hatch parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static HatchModel.Hatch parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static HatchModel.Hatch parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static HatchModel.Hatch parseFrom(
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
    public static Builder newBuilder(HatchModel.Hatch prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.command.Hatch}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.command.Hatch)
        HatchModel.HatchOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Hatch_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Hatch_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                HatchModel.Hatch.class, HatchModel.Hatch.Builder.class);
      }

      // Construct using HatchModel.Hatch.newBuilder()
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
        info_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Hatch_descriptor;
      }

      @java.lang.Override
      public HatchModel.Hatch getDefaultInstanceForType() {
        return HatchModel.Hatch.getDefaultInstance();
      }

      @java.lang.Override
      public HatchModel.Hatch build() {
        HatchModel.Hatch result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public HatchModel.Hatch buildPartial() {
        HatchModel.Hatch result = new HatchModel.Hatch(this);
        result.info_ = info_;
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
        if (other instanceof HatchModel.Hatch) {
          return mergeFrom((HatchModel.Hatch)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(HatchModel.Hatch other) {
        if (other == HatchModel.Hatch.getDefaultInstance()) return this;
        if (other.getInfo() != com.google.protobuf.ByteString.EMPTY) {
          setInfo(other.getInfo());
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
        HatchModel.Hatch parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (HatchModel.Hatch) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private com.google.protobuf.ByteString info_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes info = 1;</code>
       */
      public com.google.protobuf.ByteString getInfo() {
        return info_;
      }
      /**
       * <code>bytes info = 1;</code>
       */
      public Builder setInfo(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        info_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes info = 1;</code>
       */
      public Builder clearInfo() {

        info_ = getDefaultInstance().getInfo();
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.command.Hatch)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.command.Hatch)
    private static final HatchModel.Hatch DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new HatchModel.Hatch();
    }

    public static HatchModel.Hatch getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Hatch>
        PARSER = new com.google.protobuf.AbstractParser<Hatch>() {
      @java.lang.Override
      public Hatch parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Hatch(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Hatch> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Hatch> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public HatchModel.Hatch getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface PayloadOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.command.Payload)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>bytes tx_id = 1;</code>
     */
    com.google.protobuf.ByteString getTxId();

    /**
     * <code>string share_pubkey_hash = 2;</code>
     */
    java.lang.String getSharePubkeyHash();
    /**
     * <code>string share_pubkey_hash = 2;</code>
     */
    com.google.protobuf.ByteString
        getSharePubkeyHashBytes();

    /**
     * <code>uint32 type = 3;</code>
     */
    int getType();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.command.Payload}
   */
  public  static final class Payload extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.command.Payload)
      PayloadOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Payload.newBuilder() to construct.
    private Payload(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Payload() {
      txId_ = com.google.protobuf.ByteString.EMPTY;
      sharePubkeyHash_ = "";
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Payload(
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

              txId_ = input.readBytes();
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              sharePubkeyHash_ = s;
              break;
            }
            case 24: {

              type_ = input.readUInt32();
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
      return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Payload_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Payload_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              HatchModel.Payload.class, HatchModel.Payload.Builder.class);
    }

    public static final int TX_ID_FIELD_NUMBER = 1;
    private com.google.protobuf.ByteString txId_;
    /**
     * <code>bytes tx_id = 1;</code>
     */
    public com.google.protobuf.ByteString getTxId() {
      return txId_;
    }

    public static final int SHARE_PUBKEY_HASH_FIELD_NUMBER = 2;
    private volatile java.lang.Object sharePubkeyHash_;
    /**
     * <code>string share_pubkey_hash = 2;</code>
     */
    public java.lang.String getSharePubkeyHash() {
      java.lang.Object ref = sharePubkeyHash_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        sharePubkeyHash_ = s;
        return s;
      }
    }
    /**
     * <code>string share_pubkey_hash = 2;</code>
     */
    public com.google.protobuf.ByteString
        getSharePubkeyHashBytes() {
      java.lang.Object ref = sharePubkeyHash_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        sharePubkeyHash_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TYPE_FIELD_NUMBER = 3;
    private int type_;
    /**
     * <code>uint32 type = 3;</code>
     */
    public int getType() {
      return type_;
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
      if (!txId_.isEmpty()) {
        output.writeBytes(1, txId_);
      }
      if (!getSharePubkeyHashBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, sharePubkeyHash_);
      }
      if (type_ != 0) {
        output.writeUInt32(3, type_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!txId_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, txId_);
      }
      if (!getSharePubkeyHashBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, sharePubkeyHash_);
      }
      if (type_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(3, type_);
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
      if (!(obj instanceof HatchModel.Payload)) {
        return super.equals(obj);
      }
      HatchModel.Payload other = (HatchModel.Payload) obj;

      if (!getTxId()
          .equals(other.getTxId())) return false;
      if (!getSharePubkeyHash()
          .equals(other.getSharePubkeyHash())) return false;
      if (getType()
          != other.getType()) return false;
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
      hash = (37 * hash) + TX_ID_FIELD_NUMBER;
      hash = (53 * hash) + getTxId().hashCode();
      hash = (37 * hash) + SHARE_PUBKEY_HASH_FIELD_NUMBER;
      hash = (53 * hash) + getSharePubkeyHash().hashCode();
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getType();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static HatchModel.Payload parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static HatchModel.Payload parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static HatchModel.Payload parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static HatchModel.Payload parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static HatchModel.Payload parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static HatchModel.Payload parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static HatchModel.Payload parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static HatchModel.Payload parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static HatchModel.Payload parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static HatchModel.Payload parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static HatchModel.Payload parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static HatchModel.Payload parseFrom(
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
    public static Builder newBuilder(HatchModel.Payload prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.command.Payload}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.command.Payload)
        HatchModel.PayloadOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Payload_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Payload_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                HatchModel.Payload.class, HatchModel.Payload.Builder.class);
      }

      // Construct using HatchModel.Payload.newBuilder()
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
        txId_ = com.google.protobuf.ByteString.EMPTY;

        sharePubkeyHash_ = "";

        type_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return HatchModel.internal_static_org_ethereum_protobuf_tcp_command_Payload_descriptor;
      }

      @java.lang.Override
      public HatchModel.Payload getDefaultInstanceForType() {
        return HatchModel.Payload.getDefaultInstance();
      }

      @java.lang.Override
      public HatchModel.Payload build() {
        HatchModel.Payload result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public HatchModel.Payload buildPartial() {
        HatchModel.Payload result = new HatchModel.Payload(this);
        result.txId_ = txId_;
        result.sharePubkeyHash_ = sharePubkeyHash_;
        result.type_ = type_;
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
        if (other instanceof HatchModel.Payload) {
          return mergeFrom((HatchModel.Payload)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(HatchModel.Payload other) {
        if (other == HatchModel.Payload.getDefaultInstance()) return this;
        if (other.getTxId() != com.google.protobuf.ByteString.EMPTY) {
          setTxId(other.getTxId());
        }
        if (!other.getSharePubkeyHash().isEmpty()) {
          sharePubkeyHash_ = other.sharePubkeyHash_;
          onChanged();
        }
        if (other.getType() != 0) {
          setType(other.getType());
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
        HatchModel.Payload parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (HatchModel.Payload) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private com.google.protobuf.ByteString txId_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes tx_id = 1;</code>
       */
      public com.google.protobuf.ByteString getTxId() {
        return txId_;
      }
      /**
       * <code>bytes tx_id = 1;</code>
       */
      public Builder setTxId(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        txId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes tx_id = 1;</code>
       */
      public Builder clearTxId() {

        txId_ = getDefaultInstance().getTxId();
        onChanged();
        return this;
      }

      private java.lang.Object sharePubkeyHash_ = "";
      /**
       * <code>string share_pubkey_hash = 2;</code>
       */
      public java.lang.String getSharePubkeyHash() {
        java.lang.Object ref = sharePubkeyHash_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          sharePubkeyHash_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string share_pubkey_hash = 2;</code>
       */
      public com.google.protobuf.ByteString
          getSharePubkeyHashBytes() {
        java.lang.Object ref = sharePubkeyHash_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          sharePubkeyHash_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string share_pubkey_hash = 2;</code>
       */
      public Builder setSharePubkeyHash(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }

        sharePubkeyHash_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string share_pubkey_hash = 2;</code>
       */
      public Builder clearSharePubkeyHash() {

        sharePubkeyHash_ = getDefaultInstance().getSharePubkeyHash();
        onChanged();
        return this;
      }
      /**
       * <code>string share_pubkey_hash = 2;</code>
       */
      public Builder setSharePubkeyHashBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

        sharePubkeyHash_ = value;
        onChanged();
        return this;
      }

      private int type_ ;
      /**
       * <code>uint32 type = 3;</code>
       */
      public int getType() {
        return type_;
      }
      /**
       * <code>uint32 type = 3;</code>
       */
      public Builder setType(int value) {

        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 type = 3;</code>
       */
      public Builder clearType() {

        type_ = 0;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.command.Payload)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.command.Payload)
    private static final HatchModel.Payload DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new HatchModel.Payload();
    }

    public static HatchModel.Payload getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Payload>
        PARSER = new com.google.protobuf.AbstractParser<Payload>() {
      @java.lang.Override
      public Payload parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Payload(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Payload> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Payload> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public HatchModel.Payload getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_command_Hatch_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_command_Hatch_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_command_Payload_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_command_Payload_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\013Hatch.proto\022!org.ethereum.protobuf.tcp" +
      ".command\"\025\n\005Hatch\022\014\n\004info\030\001 \001(\014\"A\n\007Paylo" +
      "ad\022\r\n\005tx_id\030\001 \001(\014\022\031\n\021share_pubkey_hash\030\002" +
      " \001(\t\022\014\n\004type\030\003 \001(\rB\016B\nHatchModelH\001b\006prot" +
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
    internal_static_org_ethereum_protobuf_tcp_command_Hatch_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_ethereum_protobuf_tcp_command_Hatch_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_command_Hatch_descriptor,
        new java.lang.String[] { "Info", });
    internal_static_org_ethereum_protobuf_tcp_command_Payload_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_org_ethereum_protobuf_tcp_command_Payload_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_command_Payload_descriptor,
        new java.lang.String[] { "TxId", "SharePubkeyHash", "Type", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}