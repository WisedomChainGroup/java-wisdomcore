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

package org.wisdom.protobuf.tcp;

public final class ProtocolModel {
  private ProtocolModel() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface MessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.Message)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>.org.ethereum.protobuf.tcp.Message.Type type = 1;</code>
     */
    int getTypeValue();
    /**
     * <code>.org.ethereum.protobuf.tcp.Message.Type type = 1;</code>
     */
    ProtocolModel.Message.Type getType();

    /**
     * <code>bool answered = 2;</code>
     */
    boolean getAnswered();

    /**
     * <code>uint64 last_timestamp = 3;</code>
     */
    long getLastTimestamp();

    /**
     * <code>uint64 retry_times = 4;</code>
     */
    long getRetryTimes();

    /**
     * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
     */
    boolean hasP2PMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
     */
    ProtocolModel.P2PMessage getP2PMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
     */
    ProtocolModel.P2PMessageOrBuilder getP2PMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
     */
    boolean hasProtocolMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
     */
    ProtocolModel.ProtocolMessage getProtocolMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
     */
    ProtocolModel.ProtocolMessageOrBuilder getProtocolMessageOrBuilder();

    public ProtocolModel.Message.DataMsgCase getDataMsgCase();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.Message}
   */
  public  static final class Message extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.Message)
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
            case 16: {

              answered_ = input.readBool();
              break;
            }
            case 24: {

              lastTimestamp_ = input.readUInt64();
              break;
            }
            case 32: {

              retryTimes_ = input.readUInt64();
              break;
            }
            case 42: {
              ProtocolModel.P2PMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 5) {
                subBuilder = ((ProtocolModel.P2PMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.P2PMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.P2PMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 5;
              break;
            }
            case 50: {
              ProtocolModel.ProtocolMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 6) {
                subBuilder = ((ProtocolModel.ProtocolMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.ProtocolMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.ProtocolMessage) dataMsg_);
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Message_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Message_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.Message.class, ProtocolModel.Message.Builder.class);
    }

    /**
     * Protobuf enum {@code org.ethereum.protobuf.tcp.Message.Type}
     */
    public enum Type
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>P2P = 0;</code>
       */
      P2P(0),
      /**
       * <code>PROTOCOL = 1;</code>
       */
      PROTOCOL(1),
      UNRECOGNIZED(-1),
      ;

      /**
       * <code>P2P = 0;</code>
       */
      public static final int P2P_VALUE = 0;
      /**
       * <code>PROTOCOL = 1;</code>
       */
      public static final int PROTOCOL_VALUE = 1;


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
      public static Type valueOf(int value) {
        return forNumber(value);
      }

      public static Type forNumber(int value) {
        switch (value) {
          case 0: return P2P;
          case 1: return PROTOCOL;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<Type>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          Type> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<Type>() {
              public Type findValueByNumber(int number) {
                return Type.forNumber(number);
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
        return ProtocolModel.Message.getDescriptor().getEnumTypes().get(0);
      }

      private static final Type[] VALUES = values();

      public static Type valueOf(
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

      private Type(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:org.ethereum.protobuf.tcp.Message.Type)
    }

    private int dataMsgCase_ = 0;
    private java.lang.Object dataMsg_;
    public enum DataMsgCase
        implements com.google.protobuf.Internal.EnumLite {
      P2P_MESSAGE(5),
      PROTOCOL_MESSAGE(6),
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
          case 5: return P2P_MESSAGE;
          case 6: return PROTOCOL_MESSAGE;
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
     * <code>.org.ethereum.protobuf.tcp.Message.Type type = 1;</code>
     */
    public int getTypeValue() {
      return type_;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.Message.Type type = 1;</code>
     */
    public ProtocolModel.Message.Type getType() {
      @SuppressWarnings("deprecation")
      ProtocolModel.Message.Type result = ProtocolModel.Message.Type.valueOf(type_);
      return result == null ? ProtocolModel.Message.Type.UNRECOGNIZED : result;
    }

    public static final int ANSWERED_FIELD_NUMBER = 2;
    private boolean answered_;
    /**
     * <code>bool answered = 2;</code>
     */
    public boolean getAnswered() {
      return answered_;
    }

    public static final int LAST_TIMESTAMP_FIELD_NUMBER = 3;
    private long lastTimestamp_;
    /**
     * <code>uint64 last_timestamp = 3;</code>
     */
    public long getLastTimestamp() {
      return lastTimestamp_;
    }

    public static final int RETRY_TIMES_FIELD_NUMBER = 4;
    private long retryTimes_;
    /**
     * <code>uint64 retry_times = 4;</code>
     */
    public long getRetryTimes() {
      return retryTimes_;
    }

    public static final int P2P_MESSAGE_FIELD_NUMBER = 5;
    /**
     * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
     */
    public boolean hasP2PMessage() {
      return dataMsgCase_ == 5;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
     */
    public ProtocolModel.P2PMessage getP2PMessage() {
      if (dataMsgCase_ == 5) {
         return (ProtocolModel.P2PMessage) dataMsg_;
      }
      return ProtocolModel.P2PMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
     */
    public ProtocolModel.P2PMessageOrBuilder getP2PMessageOrBuilder() {
      if (dataMsgCase_ == 5) {
         return (ProtocolModel.P2PMessage) dataMsg_;
      }
      return ProtocolModel.P2PMessage.getDefaultInstance();
    }

    public static final int PROTOCOL_MESSAGE_FIELD_NUMBER = 6;
    /**
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
     */
    public boolean hasProtocolMessage() {
      return dataMsgCase_ == 6;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
     */
    public ProtocolModel.ProtocolMessage getProtocolMessage() {
      if (dataMsgCase_ == 6) {
         return (ProtocolModel.ProtocolMessage) dataMsg_;
      }
      return ProtocolModel.ProtocolMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
     */
    public ProtocolModel.ProtocolMessageOrBuilder getProtocolMessageOrBuilder() {
      if (dataMsgCase_ == 6) {
         return (ProtocolModel.ProtocolMessage) dataMsg_;
      }
      return ProtocolModel.ProtocolMessage.getDefaultInstance();
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
      if (type_ != ProtocolModel.Message.Type.P2P.getNumber()) {
        output.writeEnum(1, type_);
      }
      if (answered_ != false) {
        output.writeBool(2, answered_);
      }
      if (lastTimestamp_ != 0L) {
        output.writeUInt64(3, lastTimestamp_);
      }
      if (retryTimes_ != 0L) {
        output.writeUInt64(4, retryTimes_);
      }
      if (dataMsgCase_ == 5) {
        output.writeMessage(5, (ProtocolModel.P2PMessage) dataMsg_);
      }
      if (dataMsgCase_ == 6) {
        output.writeMessage(6, (ProtocolModel.ProtocolMessage) dataMsg_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (type_ != ProtocolModel.Message.Type.P2P.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, type_);
      }
      if (answered_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(2, answered_);
      }
      if (lastTimestamp_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(3, lastTimestamp_);
      }
      if (retryTimes_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(4, retryTimes_);
      }
      if (dataMsgCase_ == 5) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(5, (ProtocolModel.P2PMessage) dataMsg_);
      }
      if (dataMsgCase_ == 6) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(6, (ProtocolModel.ProtocolMessage) dataMsg_);
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
      if (!(obj instanceof ProtocolModel.Message)) {
        return super.equals(obj);
      }
      ProtocolModel.Message other = (ProtocolModel.Message) obj;

      if (type_ != other.type_) return false;
      if (getAnswered()
          != other.getAnswered()) return false;
      if (getLastTimestamp()
          != other.getLastTimestamp()) return false;
      if (getRetryTimes()
          != other.getRetryTimes()) return false;
      if (!getDataMsgCase().equals(other.getDataMsgCase())) return false;
      switch (dataMsgCase_) {
        case 5:
          if (!getP2PMessage()
              .equals(other.getP2PMessage())) return false;
          break;
        case 6:
          if (!getProtocolMessage()
              .equals(other.getProtocolMessage())) return false;
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
      hash = (37 * hash) + ANSWERED_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getAnswered());
      hash = (37 * hash) + LAST_TIMESTAMP_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getLastTimestamp());
      hash = (37 * hash) + RETRY_TIMES_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getRetryTimes());
      switch (dataMsgCase_) {
        case 5:
          hash = (37 * hash) + P2P_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getP2PMessage().hashCode();
          break;
        case 6:
          hash = (37 * hash) + PROTOCOL_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getProtocolMessage().hashCode();
          break;
        case 0:
        default:
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.Message parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.Message parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.Message parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.Message parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.Message parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.Message parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.Message parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.Message parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.Message parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.Message parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.Message parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.Message parseFrom(
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
    public static Builder newBuilder(ProtocolModel.Message prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.Message}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.Message)
        ProtocolModel.MessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Message_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Message_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.Message.class, ProtocolModel.Message.Builder.class);
      }

      // Construct using ProtocolModel.Message.newBuilder()
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

        answered_ = false;

        lastTimestamp_ = 0L;

        retryTimes_ = 0L;

        dataMsgCase_ = 0;
        dataMsg_ = null;
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Message_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.Message getDefaultInstanceForType() {
        return ProtocolModel.Message.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.Message build() {
        ProtocolModel.Message result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.Message buildPartial() {
        ProtocolModel.Message result = new ProtocolModel.Message(this);
        result.type_ = type_;
        result.answered_ = answered_;
        result.lastTimestamp_ = lastTimestamp_;
        result.retryTimes_ = retryTimes_;
        if (dataMsgCase_ == 5) {
          if (p2PMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = p2PMessageBuilder_.build();
          }
        }
        if (dataMsgCase_ == 6) {
          if (protocolMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = protocolMessageBuilder_.build();
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
        if (other instanceof ProtocolModel.Message) {
          return mergeFrom((ProtocolModel.Message)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.Message other) {
        if (other == ProtocolModel.Message.getDefaultInstance()) return this;
        if (other.type_ != 0) {
          setTypeValue(other.getTypeValue());
        }
        if (other.getAnswered() != false) {
          setAnswered(other.getAnswered());
        }
        if (other.getLastTimestamp() != 0L) {
          setLastTimestamp(other.getLastTimestamp());
        }
        if (other.getRetryTimes() != 0L) {
          setRetryTimes(other.getRetryTimes());
        }
        switch (other.getDataMsgCase()) {
          case P2P_MESSAGE: {
            mergeP2PMessage(other.getP2PMessage());
            break;
          }
          case PROTOCOL_MESSAGE: {
            mergeProtocolMessage(other.getProtocolMessage());
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
        ProtocolModel.Message parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.Message) e.getUnfinishedMessage();
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
       * <code>.org.ethereum.protobuf.tcp.Message.Type type = 1;</code>
       */
      public int getTypeValue() {
        return type_;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Message.Type type = 1;</code>
       */
      public Builder setTypeValue(int value) {
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Message.Type type = 1;</code>
       */
      public ProtocolModel.Message.Type getType() {
        @SuppressWarnings("deprecation")
        ProtocolModel.Message.Type result = ProtocolModel.Message.Type.valueOf(type_);
        return result == null ? ProtocolModel.Message.Type.UNRECOGNIZED : result;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Message.Type type = 1;</code>
       */
      public Builder setType(ProtocolModel.Message.Type value) {
        if (value == null) {
          throw new NullPointerException();
        }

        type_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Message.Type type = 1;</code>
       */
      public Builder clearType() {

        type_ = 0;
        onChanged();
        return this;
      }

      private boolean answered_ ;
      /**
       * <code>bool answered = 2;</code>
       */
      public boolean getAnswered() {
        return answered_;
      }
      /**
       * <code>bool answered = 2;</code>
       */
      public Builder setAnswered(boolean value) {

        answered_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bool answered = 2;</code>
       */
      public Builder clearAnswered() {

        answered_ = false;
        onChanged();
        return this;
      }

      private long lastTimestamp_ ;
      /**
       * <code>uint64 last_timestamp = 3;</code>
       */
      public long getLastTimestamp() {
        return lastTimestamp_;
      }
      /**
       * <code>uint64 last_timestamp = 3;</code>
       */
      public Builder setLastTimestamp(long value) {

        lastTimestamp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint64 last_timestamp = 3;</code>
       */
      public Builder clearLastTimestamp() {

        lastTimestamp_ = 0L;
        onChanged();
        return this;
      }

      private long retryTimes_ ;
      /**
       * <code>uint64 retry_times = 4;</code>
       */
      public long getRetryTimes() {
        return retryTimes_;
      }
      /**
       * <code>uint64 retry_times = 4;</code>
       */
      public Builder setRetryTimes(long value) {

        retryTimes_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint64 retry_times = 4;</code>
       */
      public Builder clearRetryTimes() {

        retryTimes_ = 0L;
        onChanged();
        return this;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.P2PMessage, ProtocolModel.P2PMessage.Builder, ProtocolModel.P2PMessageOrBuilder> p2PMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
       */
      public boolean hasP2PMessage() {
        return dataMsgCase_ == 5;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
       */
      public ProtocolModel.P2PMessage getP2PMessage() {
        if (p2PMessageBuilder_ == null) {
          if (dataMsgCase_ == 5) {
            return (ProtocolModel.P2PMessage) dataMsg_;
          }
          return ProtocolModel.P2PMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 5) {
            return p2PMessageBuilder_.getMessage();
          }
          return ProtocolModel.P2PMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
       */
      public Builder setP2PMessage(ProtocolModel.P2PMessage value) {
        if (p2PMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          p2PMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 5;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
       */
      public Builder setP2PMessage(
          ProtocolModel.P2PMessage.Builder builderForValue) {
        if (p2PMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          p2PMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 5;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
       */
      public Builder mergeP2PMessage(ProtocolModel.P2PMessage value) {
        if (p2PMessageBuilder_ == null) {
          if (dataMsgCase_ == 5 &&
              dataMsg_ != ProtocolModel.P2PMessage.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.P2PMessage.newBuilder((ProtocolModel.P2PMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 5) {
            p2PMessageBuilder_.mergeFrom(value);
          }
          p2PMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 5;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
       */
      public Builder clearP2PMessage() {
        if (p2PMessageBuilder_ == null) {
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
          p2PMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
       */
      public ProtocolModel.P2PMessage.Builder getP2PMessageBuilder() {
        return getP2PMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
       */
      public ProtocolModel.P2PMessageOrBuilder getP2PMessageOrBuilder() {
        if ((dataMsgCase_ == 5) && (p2PMessageBuilder_ != null)) {
          return p2PMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 5) {
            return (ProtocolModel.P2PMessage) dataMsg_;
          }
          return ProtocolModel.P2PMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage p2p_message = 5;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.P2PMessage, ProtocolModel.P2PMessage.Builder, ProtocolModel.P2PMessageOrBuilder>
          getP2PMessageFieldBuilder() {
        if (p2PMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 5)) {
            dataMsg_ = ProtocolModel.P2PMessage.getDefaultInstance();
          }
          p2PMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.P2PMessage, ProtocolModel.P2PMessage.Builder, ProtocolModel.P2PMessageOrBuilder>(
                  (ProtocolModel.P2PMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 5;
        onChanged();;
        return p2PMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.ProtocolMessage, ProtocolModel.ProtocolMessage.Builder, ProtocolModel.ProtocolMessageOrBuilder> protocolMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
       */
      public boolean hasProtocolMessage() {
        return dataMsgCase_ == 6;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
       */
      public ProtocolModel.ProtocolMessage getProtocolMessage() {
        if (protocolMessageBuilder_ == null) {
          if (dataMsgCase_ == 6) {
            return (ProtocolModel.ProtocolMessage) dataMsg_;
          }
          return ProtocolModel.ProtocolMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 6) {
            return protocolMessageBuilder_.getMessage();
          }
          return ProtocolModel.ProtocolMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
       */
      public Builder setProtocolMessage(ProtocolModel.ProtocolMessage value) {
        if (protocolMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          protocolMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 6;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
       */
      public Builder setProtocolMessage(
          ProtocolModel.ProtocolMessage.Builder builderForValue) {
        if (protocolMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          protocolMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 6;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
       */
      public Builder mergeProtocolMessage(ProtocolModel.ProtocolMessage value) {
        if (protocolMessageBuilder_ == null) {
          if (dataMsgCase_ == 6 &&
              dataMsg_ != ProtocolModel.ProtocolMessage.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.ProtocolMessage.newBuilder((ProtocolModel.ProtocolMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 6) {
            protocolMessageBuilder_.mergeFrom(value);
          }
          protocolMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 6;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
       */
      public Builder clearProtocolMessage() {
        if (protocolMessageBuilder_ == null) {
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
          protocolMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
       */
      public ProtocolModel.ProtocolMessage.Builder getProtocolMessageBuilder() {
        return getProtocolMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
       */
      public ProtocolModel.ProtocolMessageOrBuilder getProtocolMessageOrBuilder() {
        if ((dataMsgCase_ == 6) && (protocolMessageBuilder_ != null)) {
          return protocolMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 6) {
            return (ProtocolModel.ProtocolMessage) dataMsg_;
          }
          return ProtocolModel.ProtocolMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage protocol_message = 6;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.ProtocolMessage, ProtocolModel.ProtocolMessage.Builder, ProtocolModel.ProtocolMessageOrBuilder>
          getProtocolMessageFieldBuilder() {
        if (protocolMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 6)) {
            dataMsg_ = ProtocolModel.ProtocolMessage.getDefaultInstance();
          }
          protocolMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.ProtocolMessage, ProtocolModel.ProtocolMessage.Builder, ProtocolModel.ProtocolMessageOrBuilder>(
                  (ProtocolModel.ProtocolMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 6;
        onChanged();;
        return protocolMessageBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.Message)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.Message)
    private static final ProtocolModel.Message DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.Message();
    }

    public static ProtocolModel.Message getDefaultInstance() {
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
    public ProtocolModel.Message getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface TransactionsMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.TransactionsMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    java.util.List<ProtocolModel.Transaction>
        getTransactionList();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    ProtocolModel.Transaction getTransaction(int index);
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    int getTransactionCount();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    java.util.List<? extends ProtocolModel.TransactionOrBuilder>
        getTransactionOrBuilderList();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    ProtocolModel.TransactionOrBuilder getTransactionOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.TransactionsMessage}
   */
  public  static final class TransactionsMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.TransactionsMessage)
      TransactionsMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use TransactionsMessage.newBuilder() to construct.
    private TransactionsMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private TransactionsMessage() {
      transaction_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private TransactionsMessage(
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
                transaction_ = new java.util.ArrayList<ProtocolModel.Transaction>();
                mutable_bitField0_ |= 0x00000001;
              }
              transaction_.add(
                  input.readMessage(ProtocolModel.Transaction.parser(), extensionRegistry));
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
          transaction_ = java.util.Collections.unmodifiableList(transaction_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.TransactionsMessage.class, ProtocolModel.TransactionsMessage.Builder.class);
    }

    public static final int TRANSACTION_FIELD_NUMBER = 1;
    private java.util.List<ProtocolModel.Transaction> transaction_;
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    public java.util.List<ProtocolModel.Transaction> getTransactionList() {
      return transaction_;
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    public java.util.List<? extends ProtocolModel.TransactionOrBuilder>
        getTransactionOrBuilderList() {
      return transaction_;
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    public int getTransactionCount() {
      return transaction_.size();
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    public ProtocolModel.Transaction getTransaction(int index) {
      return transaction_.get(index);
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
     */
    public ProtocolModel.TransactionOrBuilder getTransactionOrBuilder(
        int index) {
      return transaction_.get(index);
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
      for (int i = 0; i < transaction_.size(); i++) {
        output.writeMessage(1, transaction_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      for (int i = 0; i < transaction_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, transaction_.get(i));
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
      if (!(obj instanceof ProtocolModel.TransactionsMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.TransactionsMessage other = (ProtocolModel.TransactionsMessage) obj;

      if (!getTransactionList()
          .equals(other.getTransactionList())) return false;
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
      if (getTransactionCount() > 0) {
        hash = (37 * hash) + TRANSACTION_FIELD_NUMBER;
        hash = (53 * hash) + getTransactionList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.TransactionsMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.TransactionsMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.TransactionsMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.TransactionsMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.TransactionsMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.TransactionsMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.TransactionsMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.TransactionsMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.TransactionsMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.TransactionsMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.TransactionsMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.TransactionsMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.TransactionsMessage prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.TransactionsMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.TransactionsMessage)
        ProtocolModel.TransactionsMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.TransactionsMessage.class, ProtocolModel.TransactionsMessage.Builder.class);
      }

      // Construct using ProtocolModel.TransactionsMessage.newBuilder()
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
          getTransactionFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        if (transactionBuilder_ == null) {
          transaction_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          transactionBuilder_.clear();
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.TransactionsMessage getDefaultInstanceForType() {
        return ProtocolModel.TransactionsMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.TransactionsMessage build() {
        ProtocolModel.TransactionsMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.TransactionsMessage buildPartial() {
        ProtocolModel.TransactionsMessage result = new ProtocolModel.TransactionsMessage(this);
        int from_bitField0_ = bitField0_;
        if (transactionBuilder_ == null) {
          if (((bitField0_ & 0x00000001) != 0)) {
            transaction_ = java.util.Collections.unmodifiableList(transaction_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.transaction_ = transaction_;
        } else {
          result.transaction_ = transactionBuilder_.build();
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
        if (other instanceof ProtocolModel.TransactionsMessage) {
          return mergeFrom((ProtocolModel.TransactionsMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.TransactionsMessage other) {
        if (other == ProtocolModel.TransactionsMessage.getDefaultInstance()) return this;
        if (transactionBuilder_ == null) {
          if (!other.transaction_.isEmpty()) {
            if (transaction_.isEmpty()) {
              transaction_ = other.transaction_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureTransactionIsMutable();
              transaction_.addAll(other.transaction_);
            }
            onChanged();
          }
        } else {
          if (!other.transaction_.isEmpty()) {
            if (transactionBuilder_.isEmpty()) {
              transactionBuilder_.dispose();
              transactionBuilder_ = null;
              transaction_ = other.transaction_;
              bitField0_ = (bitField0_ & ~0x00000001);
              transactionBuilder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getTransactionFieldBuilder() : null;
            } else {
              transactionBuilder_.addAllMessages(other.transaction_);
            }
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
        ProtocolModel.TransactionsMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.TransactionsMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.util.List<ProtocolModel.Transaction> transaction_ =
        java.util.Collections.emptyList();
      private void ensureTransactionIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          transaction_ = new java.util.ArrayList<ProtocolModel.Transaction>(transaction_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          ProtocolModel.Transaction, ProtocolModel.Transaction.Builder, ProtocolModel.TransactionOrBuilder> transactionBuilder_;

      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public java.util.List<ProtocolModel.Transaction> getTransactionList() {
        if (transactionBuilder_ == null) {
          return java.util.Collections.unmodifiableList(transaction_);
        } else {
          return transactionBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public int getTransactionCount() {
        if (transactionBuilder_ == null) {
          return transaction_.size();
        } else {
          return transactionBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public ProtocolModel.Transaction getTransaction(int index) {
        if (transactionBuilder_ == null) {
          return transaction_.get(index);
        } else {
          return transactionBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public Builder setTransaction(
          int index, ProtocolModel.Transaction value) {
        if (transactionBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureTransactionIsMutable();
          transaction_.set(index, value);
          onChanged();
        } else {
          transactionBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public Builder setTransaction(
          int index, ProtocolModel.Transaction.Builder builderForValue) {
        if (transactionBuilder_ == null) {
          ensureTransactionIsMutable();
          transaction_.set(index, builderForValue.build());
          onChanged();
        } else {
          transactionBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public Builder addTransaction(ProtocolModel.Transaction value) {
        if (transactionBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureTransactionIsMutable();
          transaction_.add(value);
          onChanged();
        } else {
          transactionBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public Builder addTransaction(
          int index, ProtocolModel.Transaction value) {
        if (transactionBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureTransactionIsMutable();
          transaction_.add(index, value);
          onChanged();
        } else {
          transactionBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public Builder addTransaction(
          ProtocolModel.Transaction.Builder builderForValue) {
        if (transactionBuilder_ == null) {
          ensureTransactionIsMutable();
          transaction_.add(builderForValue.build());
          onChanged();
        } else {
          transactionBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public Builder addTransaction(
          int index, ProtocolModel.Transaction.Builder builderForValue) {
        if (transactionBuilder_ == null) {
          ensureTransactionIsMutable();
          transaction_.add(index, builderForValue.build());
          onChanged();
        } else {
          transactionBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public Builder addAllTransaction(
          java.lang.Iterable<? extends ProtocolModel.Transaction> values) {
        if (transactionBuilder_ == null) {
          ensureTransactionIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, transaction_);
          onChanged();
        } else {
          transactionBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public Builder clearTransaction() {
        if (transactionBuilder_ == null) {
          transaction_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          transactionBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public Builder removeTransaction(int index) {
        if (transactionBuilder_ == null) {
          ensureTransactionIsMutable();
          transaction_.remove(index);
          onChanged();
        } else {
          transactionBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public ProtocolModel.Transaction.Builder getTransactionBuilder(
          int index) {
        return getTransactionFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public ProtocolModel.TransactionOrBuilder getTransactionOrBuilder(
          int index) {
        if (transactionBuilder_ == null) {
          return transaction_.get(index);  } else {
          return transactionBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public java.util.List<? extends ProtocolModel.TransactionOrBuilder>
           getTransactionOrBuilderList() {
        if (transactionBuilder_ != null) {
          return transactionBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(transaction_);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public ProtocolModel.Transaction.Builder addTransactionBuilder() {
        return getTransactionFieldBuilder().addBuilder(
            ProtocolModel.Transaction.getDefaultInstance());
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public ProtocolModel.Transaction.Builder addTransactionBuilder(
          int index) {
        return getTransactionFieldBuilder().addBuilder(
            index, ProtocolModel.Transaction.getDefaultInstance());
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction transaction = 1;</code>
       */
      public java.util.List<ProtocolModel.Transaction.Builder>
           getTransactionBuilderList() {
        return getTransactionFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          ProtocolModel.Transaction, ProtocolModel.Transaction.Builder, ProtocolModel.TransactionOrBuilder>
          getTransactionFieldBuilder() {
        if (transactionBuilder_ == null) {
          transactionBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              ProtocolModel.Transaction, ProtocolModel.Transaction.Builder, ProtocolModel.TransactionOrBuilder>(
                  transaction_,
                  ((bitField0_ & 0x00000001) != 0),
                  getParentForChildren(),
                  isClean());
          transaction_ = null;
        }
        return transactionBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.TransactionsMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.TransactionsMessage)
    private static final ProtocolModel.TransactionsMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.TransactionsMessage();
    }

    public static ProtocolModel.TransactionsMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<TransactionsMessage>
        PARSER = new com.google.protobuf.AbstractParser<TransactionsMessage>() {
      @java.lang.Override
      public TransactionsMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new TransactionsMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<TransactionsMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<TransactionsMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.TransactionsMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface ProtocolMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.ProtocolMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage.Type type = 1;</code>
     */
    int getTypeValue();
    /**
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage.Type type = 1;</code>
     */
    ProtocolModel.ProtocolMessage.Type getType();

    /**
     * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
     */
    boolean hasStatusMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
     */
    ProtocolModel.StatusMessage getStatusMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
     */
    ProtocolModel.StatusMessageOrBuilder getStatusMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
     */
    boolean hasGetBlocksMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
     */
    ProtocolModel.GetBlocksMessage getGetBlocksMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
     */
    ProtocolModel.GetBlocksMessageOrBuilder getGetBlocksMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
     */
    boolean hasBlocksMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
     */
    ProtocolModel.BlocksMessage getBlocksMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
     */
    ProtocolModel.BlocksMessageOrBuilder getBlocksMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
     */
    boolean hasCommandMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
     */
    ProtocolModel.Transaction getCommandMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
     */
    ProtocolModel.TransactionOrBuilder getCommandMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
     */
    boolean hasTransactionsMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
     */
    ProtocolModel.TransactionsMessage getTransactionsMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
     */
    ProtocolModel.TransactionsMessageOrBuilder getTransactionsMessageOrBuilder();

    public ProtocolModel.ProtocolMessage.DataMsgCase getDataMsgCase();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.ProtocolMessage}
   */
  public  static final class ProtocolMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.ProtocolMessage)
      ProtocolMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use ProtocolMessage.newBuilder() to construct.
    private ProtocolMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ProtocolMessage() {
      type_ = 0;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private ProtocolMessage(
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
              ProtocolModel.StatusMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 2) {
                subBuilder = ((ProtocolModel.StatusMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.StatusMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.StatusMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 2;
              break;
            }
            case 26: {
              ProtocolModel.GetBlocksMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 3) {
                subBuilder = ((ProtocolModel.GetBlocksMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.GetBlocksMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.GetBlocksMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 3;
              break;
            }
            case 34: {
              ProtocolModel.BlocksMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 4) {
                subBuilder = ((ProtocolModel.BlocksMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.BlocksMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.BlocksMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 4;
              break;
            }
            case 42: {
              ProtocolModel.Transaction.Builder subBuilder = null;
              if (dataMsgCase_ == 5) {
                subBuilder = ((ProtocolModel.Transaction) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.Transaction.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.Transaction) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 5;
              break;
            }
            case 50: {
              ProtocolModel.TransactionsMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 6) {
                subBuilder = ((ProtocolModel.TransactionsMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.TransactionsMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.TransactionsMessage) dataMsg_);
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.ProtocolMessage.class, ProtocolModel.ProtocolMessage.Builder.class);
    }

    /**
     * Protobuf enum {@code org.ethereum.protobuf.tcp.ProtocolMessage.Type}
     */
    public enum Type
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>STATUS = 0;</code>
       */
      STATUS(0),
      /**
       * <code>GET_BLOCKS = 1;</code>
       */
      GET_BLOCKS(1),
      /**
       * <code>BLOCKS = 2;</code>
       */
      BLOCKS(2),
      /**
       * <code>COMMAND = 3;</code>
       */
      COMMAND(3),
      /**
       * <code>TRANSACTIONS = 4;</code>
       */
      TRANSACTIONS(4),
      UNRECOGNIZED(-1),
      ;

      /**
       * <code>STATUS = 0;</code>
       */
      public static final int STATUS_VALUE = 0;
      /**
       * <code>GET_BLOCKS = 1;</code>
       */
      public static final int GET_BLOCKS_VALUE = 1;
      /**
       * <code>BLOCKS = 2;</code>
       */
      public static final int BLOCKS_VALUE = 2;
      /**
       * <code>COMMAND = 3;</code>
       */
      public static final int COMMAND_VALUE = 3;
      /**
       * <code>TRANSACTIONS = 4;</code>
       */
      public static final int TRANSACTIONS_VALUE = 4;


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
      public static Type valueOf(int value) {
        return forNumber(value);
      }

      public static Type forNumber(int value) {
        switch (value) {
          case 0: return STATUS;
          case 1: return GET_BLOCKS;
          case 2: return BLOCKS;
          case 3: return COMMAND;
          case 4: return TRANSACTIONS;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<Type>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          Type> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<Type>() {
              public Type findValueByNumber(int number) {
                return Type.forNumber(number);
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
        return ProtocolModel.ProtocolMessage.getDescriptor().getEnumTypes().get(0);
      }

      private static final Type[] VALUES = values();

      public static Type valueOf(
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

      private Type(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:org.ethereum.protobuf.tcp.ProtocolMessage.Type)
    }

    private int dataMsgCase_ = 0;
    private java.lang.Object dataMsg_;
    public enum DataMsgCase
        implements com.google.protobuf.Internal.EnumLite {
      STATUS_MESSAGE(2),
      GET_BLOCKS_MESSAGE(3),
      BLOCKS_MESSAGE(4),
      COMMAND_MESSAGE(5),
      TRANSACTIONS_MESSAGE(6),
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
          case 2: return STATUS_MESSAGE;
          case 3: return GET_BLOCKS_MESSAGE;
          case 4: return BLOCKS_MESSAGE;
          case 5: return COMMAND_MESSAGE;
          case 6: return TRANSACTIONS_MESSAGE;
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
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage.Type type = 1;</code>
     */
    public int getTypeValue() {
      return type_;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.ProtocolMessage.Type type = 1;</code>
     */
    public ProtocolModel.ProtocolMessage.Type getType() {
      @SuppressWarnings("deprecation")
      ProtocolModel.ProtocolMessage.Type result = ProtocolModel.ProtocolMessage.Type.valueOf(type_);
      return result == null ? ProtocolModel.ProtocolMessage.Type.UNRECOGNIZED : result;
    }

    public static final int STATUS_MESSAGE_FIELD_NUMBER = 2;
    /**
     * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
     */
    public boolean hasStatusMessage() {
      return dataMsgCase_ == 2;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
     */
    public ProtocolModel.StatusMessage getStatusMessage() {
      if (dataMsgCase_ == 2) {
         return (ProtocolModel.StatusMessage) dataMsg_;
      }
      return ProtocolModel.StatusMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
     */
    public ProtocolModel.StatusMessageOrBuilder getStatusMessageOrBuilder() {
      if (dataMsgCase_ == 2) {
         return (ProtocolModel.StatusMessage) dataMsg_;
      }
      return ProtocolModel.StatusMessage.getDefaultInstance();
    }

    public static final int GET_BLOCKS_MESSAGE_FIELD_NUMBER = 3;
    /**
     * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
     */
    public boolean hasGetBlocksMessage() {
      return dataMsgCase_ == 3;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
     */
    public ProtocolModel.GetBlocksMessage getGetBlocksMessage() {
      if (dataMsgCase_ == 3) {
         return (ProtocolModel.GetBlocksMessage) dataMsg_;
      }
      return ProtocolModel.GetBlocksMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
     */
    public ProtocolModel.GetBlocksMessageOrBuilder getGetBlocksMessageOrBuilder() {
      if (dataMsgCase_ == 3) {
         return (ProtocolModel.GetBlocksMessage) dataMsg_;
      }
      return ProtocolModel.GetBlocksMessage.getDefaultInstance();
    }

    public static final int BLOCKS_MESSAGE_FIELD_NUMBER = 4;
    /**
     * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
     */
    public boolean hasBlocksMessage() {
      return dataMsgCase_ == 4;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
     */
    public ProtocolModel.BlocksMessage getBlocksMessage() {
      if (dataMsgCase_ == 4) {
         return (ProtocolModel.BlocksMessage) dataMsg_;
      }
      return ProtocolModel.BlocksMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
     */
    public ProtocolModel.BlocksMessageOrBuilder getBlocksMessageOrBuilder() {
      if (dataMsgCase_ == 4) {
         return (ProtocolModel.BlocksMessage) dataMsg_;
      }
      return ProtocolModel.BlocksMessage.getDefaultInstance();
    }

    public static final int COMMAND_MESSAGE_FIELD_NUMBER = 5;
    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
     */
    public boolean hasCommandMessage() {
      return dataMsgCase_ == 5;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
     */
    public ProtocolModel.Transaction getCommandMessage() {
      if (dataMsgCase_ == 5) {
         return (ProtocolModel.Transaction) dataMsg_;
      }
      return ProtocolModel.Transaction.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
     */
    public ProtocolModel.TransactionOrBuilder getCommandMessageOrBuilder() {
      if (dataMsgCase_ == 5) {
         return (ProtocolModel.Transaction) dataMsg_;
      }
      return ProtocolModel.Transaction.getDefaultInstance();
    }

    public static final int TRANSACTIONS_MESSAGE_FIELD_NUMBER = 6;
    /**
     * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
     */
    public boolean hasTransactionsMessage() {
      return dataMsgCase_ == 6;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
     */
    public ProtocolModel.TransactionsMessage getTransactionsMessage() {
      if (dataMsgCase_ == 6) {
         return (ProtocolModel.TransactionsMessage) dataMsg_;
      }
      return ProtocolModel.TransactionsMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
     */
    public ProtocolModel.TransactionsMessageOrBuilder getTransactionsMessageOrBuilder() {
      if (dataMsgCase_ == 6) {
         return (ProtocolModel.TransactionsMessage) dataMsg_;
      }
      return ProtocolModel.TransactionsMessage.getDefaultInstance();
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
      if (type_ != ProtocolModel.ProtocolMessage.Type.STATUS.getNumber()) {
        output.writeEnum(1, type_);
      }
      if (dataMsgCase_ == 2) {
        output.writeMessage(2, (ProtocolModel.StatusMessage) dataMsg_);
      }
      if (dataMsgCase_ == 3) {
        output.writeMessage(3, (ProtocolModel.GetBlocksMessage) dataMsg_);
      }
      if (dataMsgCase_ == 4) {
        output.writeMessage(4, (ProtocolModel.BlocksMessage) dataMsg_);
      }
      if (dataMsgCase_ == 5) {
        output.writeMessage(5, (ProtocolModel.Transaction) dataMsg_);
      }
      if (dataMsgCase_ == 6) {
        output.writeMessage(6, (ProtocolModel.TransactionsMessage) dataMsg_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (type_ != ProtocolModel.ProtocolMessage.Type.STATUS.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, type_);
      }
      if (dataMsgCase_ == 2) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, (ProtocolModel.StatusMessage) dataMsg_);
      }
      if (dataMsgCase_ == 3) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, (ProtocolModel.GetBlocksMessage) dataMsg_);
      }
      if (dataMsgCase_ == 4) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, (ProtocolModel.BlocksMessage) dataMsg_);
      }
      if (dataMsgCase_ == 5) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(5, (ProtocolModel.Transaction) dataMsg_);
      }
      if (dataMsgCase_ == 6) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(6, (ProtocolModel.TransactionsMessage) dataMsg_);
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
      if (!(obj instanceof ProtocolModel.ProtocolMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.ProtocolMessage other = (ProtocolModel.ProtocolMessage) obj;

      if (type_ != other.type_) return false;
      if (!getDataMsgCase().equals(other.getDataMsgCase())) return false;
      switch (dataMsgCase_) {
        case 2:
          if (!getStatusMessage()
              .equals(other.getStatusMessage())) return false;
          break;
        case 3:
          if (!getGetBlocksMessage()
              .equals(other.getGetBlocksMessage())) return false;
          break;
        case 4:
          if (!getBlocksMessage()
              .equals(other.getBlocksMessage())) return false;
          break;
        case 5:
          if (!getCommandMessage()
              .equals(other.getCommandMessage())) return false;
          break;
        case 6:
          if (!getTransactionsMessage()
              .equals(other.getTransactionsMessage())) return false;
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
      switch (dataMsgCase_) {
        case 2:
          hash = (37 * hash) + STATUS_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getStatusMessage().hashCode();
          break;
        case 3:
          hash = (37 * hash) + GET_BLOCKS_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getGetBlocksMessage().hashCode();
          break;
        case 4:
          hash = (37 * hash) + BLOCKS_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getBlocksMessage().hashCode();
          break;
        case 5:
          hash = (37 * hash) + COMMAND_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getCommandMessage().hashCode();
          break;
        case 6:
          hash = (37 * hash) + TRANSACTIONS_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getTransactionsMessage().hashCode();
          break;
        case 0:
        default:
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.ProtocolMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.ProtocolMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.ProtocolMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.ProtocolMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.ProtocolMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.ProtocolMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.ProtocolMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.ProtocolMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.ProtocolMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.ProtocolMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.ProtocolMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.ProtocolMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.ProtocolMessage prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.ProtocolMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.ProtocolMessage)
        ProtocolModel.ProtocolMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.ProtocolMessage.class, ProtocolModel.ProtocolMessage.Builder.class);
      }

      // Construct using ProtocolModel.ProtocolMessage.newBuilder()
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

        dataMsgCase_ = 0;
        dataMsg_ = null;
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.ProtocolMessage getDefaultInstanceForType() {
        return ProtocolModel.ProtocolMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.ProtocolMessage build() {
        ProtocolModel.ProtocolMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.ProtocolMessage buildPartial() {
        ProtocolModel.ProtocolMessage result = new ProtocolModel.ProtocolMessage(this);
        result.type_ = type_;
        if (dataMsgCase_ == 2) {
          if (statusMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = statusMessageBuilder_.build();
          }
        }
        if (dataMsgCase_ == 3) {
          if (getBlocksMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = getBlocksMessageBuilder_.build();
          }
        }
        if (dataMsgCase_ == 4) {
          if (blocksMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = blocksMessageBuilder_.build();
          }
        }
        if (dataMsgCase_ == 5) {
          if (commandMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = commandMessageBuilder_.build();
          }
        }
        if (dataMsgCase_ == 6) {
          if (transactionsMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = transactionsMessageBuilder_.build();
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
        if (other instanceof ProtocolModel.ProtocolMessage) {
          return mergeFrom((ProtocolModel.ProtocolMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.ProtocolMessage other) {
        if (other == ProtocolModel.ProtocolMessage.getDefaultInstance()) return this;
        if (other.type_ != 0) {
          setTypeValue(other.getTypeValue());
        }
        switch (other.getDataMsgCase()) {
          case STATUS_MESSAGE: {
            mergeStatusMessage(other.getStatusMessage());
            break;
          }
          case GET_BLOCKS_MESSAGE: {
            mergeGetBlocksMessage(other.getGetBlocksMessage());
            break;
          }
          case BLOCKS_MESSAGE: {
            mergeBlocksMessage(other.getBlocksMessage());
            break;
          }
          case COMMAND_MESSAGE: {
            mergeCommandMessage(other.getCommandMessage());
            break;
          }
          case TRANSACTIONS_MESSAGE: {
            mergeTransactionsMessage(other.getTransactionsMessage());
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
        ProtocolModel.ProtocolMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.ProtocolMessage) e.getUnfinishedMessage();
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
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage.Type type = 1;</code>
       */
      public int getTypeValue() {
        return type_;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage.Type type = 1;</code>
       */
      public Builder setTypeValue(int value) {
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage.Type type = 1;</code>
       */
      public ProtocolModel.ProtocolMessage.Type getType() {
        @SuppressWarnings("deprecation")
        ProtocolModel.ProtocolMessage.Type result = ProtocolModel.ProtocolMessage.Type.valueOf(type_);
        return result == null ? ProtocolModel.ProtocolMessage.Type.UNRECOGNIZED : result;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage.Type type = 1;</code>
       */
      public Builder setType(ProtocolModel.ProtocolMessage.Type value) {
        if (value == null) {
          throw new NullPointerException();
        }

        type_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.ProtocolMessage.Type type = 1;</code>
       */
      public Builder clearType() {

        type_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.StatusMessage, ProtocolModel.StatusMessage.Builder, ProtocolModel.StatusMessageOrBuilder> statusMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
       */
      public boolean hasStatusMessage() {
        return dataMsgCase_ == 2;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
       */
      public ProtocolModel.StatusMessage getStatusMessage() {
        if (statusMessageBuilder_ == null) {
          if (dataMsgCase_ == 2) {
            return (ProtocolModel.StatusMessage) dataMsg_;
          }
          return ProtocolModel.StatusMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 2) {
            return statusMessageBuilder_.getMessage();
          }
          return ProtocolModel.StatusMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
       */
      public Builder setStatusMessage(ProtocolModel.StatusMessage value) {
        if (statusMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          statusMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 2;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
       */
      public Builder setStatusMessage(
          ProtocolModel.StatusMessage.Builder builderForValue) {
        if (statusMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          statusMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 2;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
       */
      public Builder mergeStatusMessage(ProtocolModel.StatusMessage value) {
        if (statusMessageBuilder_ == null) {
          if (dataMsgCase_ == 2 &&
              dataMsg_ != ProtocolModel.StatusMessage.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.StatusMessage.newBuilder((ProtocolModel.StatusMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 2) {
            statusMessageBuilder_.mergeFrom(value);
          }
          statusMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 2;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
       */
      public Builder clearStatusMessage() {
        if (statusMessageBuilder_ == null) {
          if (dataMsgCase_ == 2) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
            onChanged();
          }
        } else {
          if (dataMsgCase_ == 2) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
          }
          statusMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
       */
      public ProtocolModel.StatusMessage.Builder getStatusMessageBuilder() {
        return getStatusMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
       */
      public ProtocolModel.StatusMessageOrBuilder getStatusMessageOrBuilder() {
        if ((dataMsgCase_ == 2) && (statusMessageBuilder_ != null)) {
          return statusMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 2) {
            return (ProtocolModel.StatusMessage) dataMsg_;
          }
          return ProtocolModel.StatusMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.StatusMessage status_message = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.StatusMessage, ProtocolModel.StatusMessage.Builder, ProtocolModel.StatusMessageOrBuilder>
          getStatusMessageFieldBuilder() {
        if (statusMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 2)) {
            dataMsg_ = ProtocolModel.StatusMessage.getDefaultInstance();
          }
          statusMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.StatusMessage, ProtocolModel.StatusMessage.Builder, ProtocolModel.StatusMessageOrBuilder>(
                  (ProtocolModel.StatusMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 2;
        onChanged();;
        return statusMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.GetBlocksMessage, ProtocolModel.GetBlocksMessage.Builder, ProtocolModel.GetBlocksMessageOrBuilder> getBlocksMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
       */
      public boolean hasGetBlocksMessage() {
        return dataMsgCase_ == 3;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
       */
      public ProtocolModel.GetBlocksMessage getGetBlocksMessage() {
        if (getBlocksMessageBuilder_ == null) {
          if (dataMsgCase_ == 3) {
            return (ProtocolModel.GetBlocksMessage) dataMsg_;
          }
          return ProtocolModel.GetBlocksMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 3) {
            return getBlocksMessageBuilder_.getMessage();
          }
          return ProtocolModel.GetBlocksMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
       */
      public Builder setGetBlocksMessage(ProtocolModel.GetBlocksMessage value) {
        if (getBlocksMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          getBlocksMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 3;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
       */
      public Builder setGetBlocksMessage(
          ProtocolModel.GetBlocksMessage.Builder builderForValue) {
        if (getBlocksMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          getBlocksMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 3;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
       */
      public Builder mergeGetBlocksMessage(ProtocolModel.GetBlocksMessage value) {
        if (getBlocksMessageBuilder_ == null) {
          if (dataMsgCase_ == 3 &&
              dataMsg_ != ProtocolModel.GetBlocksMessage.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.GetBlocksMessage.newBuilder((ProtocolModel.GetBlocksMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 3) {
            getBlocksMessageBuilder_.mergeFrom(value);
          }
          getBlocksMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 3;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
       */
      public Builder clearGetBlocksMessage() {
        if (getBlocksMessageBuilder_ == null) {
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
          getBlocksMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
       */
      public ProtocolModel.GetBlocksMessage.Builder getGetBlocksMessageBuilder() {
        return getGetBlocksMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
       */
      public ProtocolModel.GetBlocksMessageOrBuilder getGetBlocksMessageOrBuilder() {
        if ((dataMsgCase_ == 3) && (getBlocksMessageBuilder_ != null)) {
          return getBlocksMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 3) {
            return (ProtocolModel.GetBlocksMessage) dataMsg_;
          }
          return ProtocolModel.GetBlocksMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.GetBlocksMessage get_blocks_message = 3;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.GetBlocksMessage, ProtocolModel.GetBlocksMessage.Builder, ProtocolModel.GetBlocksMessageOrBuilder>
          getGetBlocksMessageFieldBuilder() {
        if (getBlocksMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 3)) {
            dataMsg_ = ProtocolModel.GetBlocksMessage.getDefaultInstance();
          }
          getBlocksMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.GetBlocksMessage, ProtocolModel.GetBlocksMessage.Builder, ProtocolModel.GetBlocksMessageOrBuilder>(
                  (ProtocolModel.GetBlocksMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 3;
        onChanged();;
        return getBlocksMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.BlocksMessage, ProtocolModel.BlocksMessage.Builder, ProtocolModel.BlocksMessageOrBuilder> blocksMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
       */
      public boolean hasBlocksMessage() {
        return dataMsgCase_ == 4;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
       */
      public ProtocolModel.BlocksMessage getBlocksMessage() {
        if (blocksMessageBuilder_ == null) {
          if (dataMsgCase_ == 4) {
            return (ProtocolModel.BlocksMessage) dataMsg_;
          }
          return ProtocolModel.BlocksMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 4) {
            return blocksMessageBuilder_.getMessage();
          }
          return ProtocolModel.BlocksMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
       */
      public Builder setBlocksMessage(ProtocolModel.BlocksMessage value) {
        if (blocksMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          blocksMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 4;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
       */
      public Builder setBlocksMessage(
          ProtocolModel.BlocksMessage.Builder builderForValue) {
        if (blocksMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          blocksMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 4;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
       */
      public Builder mergeBlocksMessage(ProtocolModel.BlocksMessage value) {
        if (blocksMessageBuilder_ == null) {
          if (dataMsgCase_ == 4 &&
              dataMsg_ != ProtocolModel.BlocksMessage.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.BlocksMessage.newBuilder((ProtocolModel.BlocksMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 4) {
            blocksMessageBuilder_.mergeFrom(value);
          }
          blocksMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 4;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
       */
      public Builder clearBlocksMessage() {
        if (blocksMessageBuilder_ == null) {
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
          blocksMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
       */
      public ProtocolModel.BlocksMessage.Builder getBlocksMessageBuilder() {
        return getBlocksMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
       */
      public ProtocolModel.BlocksMessageOrBuilder getBlocksMessageOrBuilder() {
        if ((dataMsgCase_ == 4) && (blocksMessageBuilder_ != null)) {
          return blocksMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 4) {
            return (ProtocolModel.BlocksMessage) dataMsg_;
          }
          return ProtocolModel.BlocksMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.BlocksMessage blocks_message = 4;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.BlocksMessage, ProtocolModel.BlocksMessage.Builder, ProtocolModel.BlocksMessageOrBuilder>
          getBlocksMessageFieldBuilder() {
        if (blocksMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 4)) {
            dataMsg_ = ProtocolModel.BlocksMessage.getDefaultInstance();
          }
          blocksMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.BlocksMessage, ProtocolModel.BlocksMessage.Builder, ProtocolModel.BlocksMessageOrBuilder>(
                  (ProtocolModel.BlocksMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 4;
        onChanged();;
        return blocksMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.Transaction, ProtocolModel.Transaction.Builder, ProtocolModel.TransactionOrBuilder> commandMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
       */
      public boolean hasCommandMessage() {
        return dataMsgCase_ == 5;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
       */
      public ProtocolModel.Transaction getCommandMessage() {
        if (commandMessageBuilder_ == null) {
          if (dataMsgCase_ == 5) {
            return (ProtocolModel.Transaction) dataMsg_;
          }
          return ProtocolModel.Transaction.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 5) {
            return commandMessageBuilder_.getMessage();
          }
          return ProtocolModel.Transaction.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
       */
      public Builder setCommandMessage(ProtocolModel.Transaction value) {
        if (commandMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          commandMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 5;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
       */
      public Builder setCommandMessage(
          ProtocolModel.Transaction.Builder builderForValue) {
        if (commandMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          commandMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 5;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
       */
      public Builder mergeCommandMessage(ProtocolModel.Transaction value) {
        if (commandMessageBuilder_ == null) {
          if (dataMsgCase_ == 5 &&
              dataMsg_ != ProtocolModel.Transaction.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.Transaction.newBuilder((ProtocolModel.Transaction) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 5) {
            commandMessageBuilder_.mergeFrom(value);
          }
          commandMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 5;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
       */
      public Builder clearCommandMessage() {
        if (commandMessageBuilder_ == null) {
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
          commandMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
       */
      public ProtocolModel.Transaction.Builder getCommandMessageBuilder() {
        return getCommandMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
       */
      public ProtocolModel.TransactionOrBuilder getCommandMessageOrBuilder() {
        if ((dataMsgCase_ == 5) && (commandMessageBuilder_ != null)) {
          return commandMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 5) {
            return (ProtocolModel.Transaction) dataMsg_;
          }
          return ProtocolModel.Transaction.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction command_message = 5;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.Transaction, ProtocolModel.Transaction.Builder, ProtocolModel.TransactionOrBuilder>
          getCommandMessageFieldBuilder() {
        if (commandMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 5)) {
            dataMsg_ = ProtocolModel.Transaction.getDefaultInstance();
          }
          commandMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.Transaction, ProtocolModel.Transaction.Builder, ProtocolModel.TransactionOrBuilder>(
                  (ProtocolModel.Transaction) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 5;
        onChanged();;
        return commandMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.TransactionsMessage, ProtocolModel.TransactionsMessage.Builder, ProtocolModel.TransactionsMessageOrBuilder> transactionsMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
       */
      public boolean hasTransactionsMessage() {
        return dataMsgCase_ == 6;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
       */
      public ProtocolModel.TransactionsMessage getTransactionsMessage() {
        if (transactionsMessageBuilder_ == null) {
          if (dataMsgCase_ == 6) {
            return (ProtocolModel.TransactionsMessage) dataMsg_;
          }
          return ProtocolModel.TransactionsMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 6) {
            return transactionsMessageBuilder_.getMessage();
          }
          return ProtocolModel.TransactionsMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
       */
      public Builder setTransactionsMessage(ProtocolModel.TransactionsMessage value) {
        if (transactionsMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          transactionsMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 6;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
       */
      public Builder setTransactionsMessage(
          ProtocolModel.TransactionsMessage.Builder builderForValue) {
        if (transactionsMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          transactionsMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 6;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
       */
      public Builder mergeTransactionsMessage(ProtocolModel.TransactionsMessage value) {
        if (transactionsMessageBuilder_ == null) {
          if (dataMsgCase_ == 6 &&
              dataMsg_ != ProtocolModel.TransactionsMessage.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.TransactionsMessage.newBuilder((ProtocolModel.TransactionsMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 6) {
            transactionsMessageBuilder_.mergeFrom(value);
          }
          transactionsMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 6;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
       */
      public Builder clearTransactionsMessage() {
        if (transactionsMessageBuilder_ == null) {
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
          transactionsMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
       */
      public ProtocolModel.TransactionsMessage.Builder getTransactionsMessageBuilder() {
        return getTransactionsMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
       */
      public ProtocolModel.TransactionsMessageOrBuilder getTransactionsMessageOrBuilder() {
        if ((dataMsgCase_ == 6) && (transactionsMessageBuilder_ != null)) {
          return transactionsMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 6) {
            return (ProtocolModel.TransactionsMessage) dataMsg_;
          }
          return ProtocolModel.TransactionsMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.TransactionsMessage transactions_message = 6;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.TransactionsMessage, ProtocolModel.TransactionsMessage.Builder, ProtocolModel.TransactionsMessageOrBuilder>
          getTransactionsMessageFieldBuilder() {
        if (transactionsMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 6)) {
            dataMsg_ = ProtocolModel.TransactionsMessage.getDefaultInstance();
          }
          transactionsMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.TransactionsMessage, ProtocolModel.TransactionsMessage.Builder, ProtocolModel.TransactionsMessageOrBuilder>(
                  (ProtocolModel.TransactionsMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 6;
        onChanged();;
        return transactionsMessageBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.ProtocolMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.ProtocolMessage)
    private static final ProtocolModel.ProtocolMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.ProtocolMessage();
    }

    public static ProtocolModel.ProtocolMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ProtocolMessage>
        PARSER = new com.google.protobuf.AbstractParser<ProtocolMessage>() {
      @java.lang.Override
      public ProtocolMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ProtocolMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ProtocolMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ProtocolMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.ProtocolMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface BlockOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.Block)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 version = 1;</code>
     */
    int getVersion();

    /**
     * <code>bytes hash_prev_block = 2;</code>
     */
    com.google.protobuf.ByteString getHashPrevBlock();

    /**
     * <code>bytes hash_merkle_root = 3;</code>
     */
    com.google.protobuf.ByteString getHashMerkleRoot();

    /**
     * <code>bytes hash_merkle_state = 4;</code>
     */
    com.google.protobuf.ByteString getHashMerkleState();

    /**
     * <code>bytes hash_merkle_incubate = 5;</code>
     */
    com.google.protobuf.ByteString getHashMerkleIncubate();

    /**
     * <code>uint32 height = 6;</code>
     */
    int getHeight();

    /**
     * <code>uint32 created_at = 7;</code>
     */
    int getCreatedAt();

    /**
     * <code>bytes n_bits = 8;</code>
     */
    com.google.protobuf.ByteString getNBits();

    /**
     * <code>bytes nonce = 9;</code>
     */
    com.google.protobuf.ByteString getNonce();

    /**
     * <code>bytes block_notice = 10;</code>
     */
    com.google.protobuf.ByteString getBlockNotice();

    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    java.util.List<ProtocolModel.Transaction>
        getBodyList();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    ProtocolModel.Transaction getBody(int index);
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    int getBodyCount();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    java.util.List<? extends ProtocolModel.TransactionOrBuilder>
        getBodyOrBuilderList();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    ProtocolModel.TransactionOrBuilder getBodyOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.Block}
   */
  public  static final class Block extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.Block)
      BlockOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Block.newBuilder() to construct.
    private Block(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Block() {
      hashPrevBlock_ = com.google.protobuf.ByteString.EMPTY;
      hashMerkleRoot_ = com.google.protobuf.ByteString.EMPTY;
      hashMerkleState_ = com.google.protobuf.ByteString.EMPTY;
      hashMerkleIncubate_ = com.google.protobuf.ByteString.EMPTY;
      nBits_ = com.google.protobuf.ByteString.EMPTY;
      nonce_ = com.google.protobuf.ByteString.EMPTY;
      blockNotice_ = com.google.protobuf.ByteString.EMPTY;
      body_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Block(
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

              version_ = input.readUInt32();
              break;
            }
            case 18: {

              hashPrevBlock_ = input.readBytes();
              break;
            }
            case 26: {

              hashMerkleRoot_ = input.readBytes();
              break;
            }
            case 34: {

              hashMerkleState_ = input.readBytes();
              break;
            }
            case 42: {

              hashMerkleIncubate_ = input.readBytes();
              break;
            }
            case 48: {

              height_ = input.readUInt32();
              break;
            }
            case 56: {

              createdAt_ = input.readUInt32();
              break;
            }
            case 66: {

              nBits_ = input.readBytes();
              break;
            }
            case 74: {

              nonce_ = input.readBytes();
              break;
            }
            case 82: {

              blockNotice_ = input.readBytes();
              break;
            }
            case 90: {
              if (!((mutable_bitField0_ & 0x00000400) != 0)) {
                body_ = new java.util.ArrayList<ProtocolModel.Transaction>();
                mutable_bitField0_ |= 0x00000400;
              }
              body_.add(
                  input.readMessage(ProtocolModel.Transaction.parser(), extensionRegistry));
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
        if (((mutable_bitField0_ & 0x00000400) != 0)) {
          body_ = java.util.Collections.unmodifiableList(body_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Block_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Block_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.Block.class, ProtocolModel.Block.Builder.class);
    }

    private int bitField0_;
    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_;
    /**
     * <code>uint32 version = 1;</code>
     */
    public int getVersion() {
      return version_;
    }

    public static final int HASH_PREV_BLOCK_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString hashPrevBlock_;
    /**
     * <code>bytes hash_prev_block = 2;</code>
     */
    public com.google.protobuf.ByteString getHashPrevBlock() {
      return hashPrevBlock_;
    }

    public static final int HASH_MERKLE_ROOT_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString hashMerkleRoot_;
    /**
     * <code>bytes hash_merkle_root = 3;</code>
     */
    public com.google.protobuf.ByteString getHashMerkleRoot() {
      return hashMerkleRoot_;
    }

    public static final int HASH_MERKLE_STATE_FIELD_NUMBER = 4;
    private com.google.protobuf.ByteString hashMerkleState_;
    /**
     * <code>bytes hash_merkle_state = 4;</code>
     */
    public com.google.protobuf.ByteString getHashMerkleState() {
      return hashMerkleState_;
    }

    public static final int HASH_MERKLE_INCUBATE_FIELD_NUMBER = 5;
    private com.google.protobuf.ByteString hashMerkleIncubate_;
    /**
     * <code>bytes hash_merkle_incubate = 5;</code>
     */
    public com.google.protobuf.ByteString getHashMerkleIncubate() {
      return hashMerkleIncubate_;
    }

    public static final int HEIGHT_FIELD_NUMBER = 6;
    private int height_;
    /**
     * <code>uint32 height = 6;</code>
     */
    public int getHeight() {
      return height_;
    }

    public static final int CREATED_AT_FIELD_NUMBER = 7;
    private int createdAt_;
    /**
     * <code>uint32 created_at = 7;</code>
     */
    public int getCreatedAt() {
      return createdAt_;
    }

    public static final int N_BITS_FIELD_NUMBER = 8;
    private com.google.protobuf.ByteString nBits_;
    /**
     * <code>bytes n_bits = 8;</code>
     */
    public com.google.protobuf.ByteString getNBits() {
      return nBits_;
    }

    public static final int NONCE_FIELD_NUMBER = 9;
    private com.google.protobuf.ByteString nonce_;
    /**
     * <code>bytes nonce = 9;</code>
     */
    public com.google.protobuf.ByteString getNonce() {
      return nonce_;
    }

    public static final int BLOCK_NOTICE_FIELD_NUMBER = 10;
    private com.google.protobuf.ByteString blockNotice_;
    /**
     * <code>bytes block_notice = 10;</code>
     */
    public com.google.protobuf.ByteString getBlockNotice() {
      return blockNotice_;
    }

    public static final int BODY_FIELD_NUMBER = 11;
    private java.util.List<ProtocolModel.Transaction> body_;
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    public java.util.List<ProtocolModel.Transaction> getBodyList() {
      return body_;
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    public java.util.List<? extends ProtocolModel.TransactionOrBuilder>
        getBodyOrBuilderList() {
      return body_;
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    public int getBodyCount() {
      return body_.size();
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    public ProtocolModel.Transaction getBody(int index) {
      return body_.get(index);
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
     */
    public ProtocolModel.TransactionOrBuilder getBodyOrBuilder(
        int index) {
      return body_.get(index);
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
      if (version_ != 0) {
        output.writeUInt32(1, version_);
      }
      if (!hashPrevBlock_.isEmpty()) {
        output.writeBytes(2, hashPrevBlock_);
      }
      if (!hashMerkleRoot_.isEmpty()) {
        output.writeBytes(3, hashMerkleRoot_);
      }
      if (!hashMerkleState_.isEmpty()) {
        output.writeBytes(4, hashMerkleState_);
      }
      if (!hashMerkleIncubate_.isEmpty()) {
        output.writeBytes(5, hashMerkleIncubate_);
      }
      if (height_ != 0) {
        output.writeUInt32(6, height_);
      }
      if (createdAt_ != 0) {
        output.writeUInt32(7, createdAt_);
      }
      if (!nBits_.isEmpty()) {
        output.writeBytes(8, nBits_);
      }
      if (!nonce_.isEmpty()) {
        output.writeBytes(9, nonce_);
      }
      if (!blockNotice_.isEmpty()) {
        output.writeBytes(10, blockNotice_);
      }
      for (int i = 0; i < body_.size(); i++) {
        output.writeMessage(11, body_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (version_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, version_);
      }
      if (!hashPrevBlock_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, hashPrevBlock_);
      }
      if (!hashMerkleRoot_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, hashMerkleRoot_);
      }
      if (!hashMerkleState_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(4, hashMerkleState_);
      }
      if (!hashMerkleIncubate_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(5, hashMerkleIncubate_);
      }
      if (height_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(6, height_);
      }
      if (createdAt_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(7, createdAt_);
      }
      if (!nBits_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(8, nBits_);
      }
      if (!nonce_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(9, nonce_);
      }
      if (!blockNotice_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(10, blockNotice_);
      }
      for (int i = 0; i < body_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(11, body_.get(i));
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
      if (!(obj instanceof ProtocolModel.Block)) {
        return super.equals(obj);
      }
      ProtocolModel.Block other = (ProtocolModel.Block) obj;

      if (getVersion()
          != other.getVersion()) return false;
      if (!getHashPrevBlock()
          .equals(other.getHashPrevBlock())) return false;
      if (!getHashMerkleRoot()
          .equals(other.getHashMerkleRoot())) return false;
      if (!getHashMerkleState()
          .equals(other.getHashMerkleState())) return false;
      if (!getHashMerkleIncubate()
          .equals(other.getHashMerkleIncubate())) return false;
      if (getHeight()
          != other.getHeight()) return false;
      if (getCreatedAt()
          != other.getCreatedAt()) return false;
      if (!getNBits()
          .equals(other.getNBits())) return false;
      if (!getNonce()
          .equals(other.getNonce())) return false;
      if (!getBlockNotice()
          .equals(other.getBlockNotice())) return false;
      if (!getBodyList()
          .equals(other.getBodyList())) return false;
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
      hash = (37 * hash) + VERSION_FIELD_NUMBER;
      hash = (53 * hash) + getVersion();
      hash = (37 * hash) + HASH_PREV_BLOCK_FIELD_NUMBER;
      hash = (53 * hash) + getHashPrevBlock().hashCode();
      hash = (37 * hash) + HASH_MERKLE_ROOT_FIELD_NUMBER;
      hash = (53 * hash) + getHashMerkleRoot().hashCode();
      hash = (37 * hash) + HASH_MERKLE_STATE_FIELD_NUMBER;
      hash = (53 * hash) + getHashMerkleState().hashCode();
      hash = (37 * hash) + HASH_MERKLE_INCUBATE_FIELD_NUMBER;
      hash = (53 * hash) + getHashMerkleIncubate().hashCode();
      hash = (37 * hash) + HEIGHT_FIELD_NUMBER;
      hash = (53 * hash) + getHeight();
      hash = (37 * hash) + CREATED_AT_FIELD_NUMBER;
      hash = (53 * hash) + getCreatedAt();
      hash = (37 * hash) + N_BITS_FIELD_NUMBER;
      hash = (53 * hash) + getNBits().hashCode();
      hash = (37 * hash) + NONCE_FIELD_NUMBER;
      hash = (53 * hash) + getNonce().hashCode();
      hash = (37 * hash) + BLOCK_NOTICE_FIELD_NUMBER;
      hash = (53 * hash) + getBlockNotice().hashCode();
      if (getBodyCount() > 0) {
        hash = (37 * hash) + BODY_FIELD_NUMBER;
        hash = (53 * hash) + getBodyList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.Block parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.Block parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.Block parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.Block parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.Block parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.Block parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.Block parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.Block parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.Block parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.Block parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.Block parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.Block parseFrom(
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
    public static Builder newBuilder(ProtocolModel.Block prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.Block}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.Block)
        ProtocolModel.BlockOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Block_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Block_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.Block.class, ProtocolModel.Block.Builder.class);
      }

      // Construct using ProtocolModel.Block.newBuilder()
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
          getBodyFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        version_ = 0;

        hashPrevBlock_ = com.google.protobuf.ByteString.EMPTY;

        hashMerkleRoot_ = com.google.protobuf.ByteString.EMPTY;

        hashMerkleState_ = com.google.protobuf.ByteString.EMPTY;

        hashMerkleIncubate_ = com.google.protobuf.ByteString.EMPTY;

        height_ = 0;

        createdAt_ = 0;

        nBits_ = com.google.protobuf.ByteString.EMPTY;

        nonce_ = com.google.protobuf.ByteString.EMPTY;

        blockNotice_ = com.google.protobuf.ByteString.EMPTY;

        if (bodyBuilder_ == null) {
          body_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000400);
        } else {
          bodyBuilder_.clear();
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Block_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.Block getDefaultInstanceForType() {
        return ProtocolModel.Block.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.Block build() {
        ProtocolModel.Block result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.Block buildPartial() {
        ProtocolModel.Block result = new ProtocolModel.Block(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.version_ = version_;
        result.hashPrevBlock_ = hashPrevBlock_;
        result.hashMerkleRoot_ = hashMerkleRoot_;
        result.hashMerkleState_ = hashMerkleState_;
        result.hashMerkleIncubate_ = hashMerkleIncubate_;
        result.height_ = height_;
        result.createdAt_ = createdAt_;
        result.nBits_ = nBits_;
        result.nonce_ = nonce_;
        result.blockNotice_ = blockNotice_;
        if (bodyBuilder_ == null) {
          if (((bitField0_ & 0x00000400) != 0)) {
            body_ = java.util.Collections.unmodifiableList(body_);
            bitField0_ = (bitField0_ & ~0x00000400);
          }
          result.body_ = body_;
        } else {
          result.body_ = bodyBuilder_.build();
        }
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
        if (other instanceof ProtocolModel.Block) {
          return mergeFrom((ProtocolModel.Block)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.Block other) {
        if (other == ProtocolModel.Block.getDefaultInstance()) return this;
        if (other.getVersion() != 0) {
          setVersion(other.getVersion());
        }
        if (other.getHashPrevBlock() != com.google.protobuf.ByteString.EMPTY) {
          setHashPrevBlock(other.getHashPrevBlock());
        }
        if (other.getHashMerkleRoot() != com.google.protobuf.ByteString.EMPTY) {
          setHashMerkleRoot(other.getHashMerkleRoot());
        }
        if (other.getHashMerkleState() != com.google.protobuf.ByteString.EMPTY) {
          setHashMerkleState(other.getHashMerkleState());
        }
        if (other.getHashMerkleIncubate() != com.google.protobuf.ByteString.EMPTY) {
          setHashMerkleIncubate(other.getHashMerkleIncubate());
        }
        if (other.getHeight() != 0) {
          setHeight(other.getHeight());
        }
        if (other.getCreatedAt() != 0) {
          setCreatedAt(other.getCreatedAt());
        }
        if (other.getNBits() != com.google.protobuf.ByteString.EMPTY) {
          setNBits(other.getNBits());
        }
        if (other.getNonce() != com.google.protobuf.ByteString.EMPTY) {
          setNonce(other.getNonce());
        }
        if (other.getBlockNotice() != com.google.protobuf.ByteString.EMPTY) {
          setBlockNotice(other.getBlockNotice());
        }
        if (bodyBuilder_ == null) {
          if (!other.body_.isEmpty()) {
            if (body_.isEmpty()) {
              body_ = other.body_;
              bitField0_ = (bitField0_ & ~0x00000400);
            } else {
              ensureBodyIsMutable();
              body_.addAll(other.body_);
            }
            onChanged();
          }
        } else {
          if (!other.body_.isEmpty()) {
            if (bodyBuilder_.isEmpty()) {
              bodyBuilder_.dispose();
              bodyBuilder_ = null;
              body_ = other.body_;
              bitField0_ = (bitField0_ & ~0x00000400);
              bodyBuilder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getBodyFieldBuilder() : null;
            } else {
              bodyBuilder_.addAllMessages(other.body_);
            }
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
        ProtocolModel.Block parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.Block) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int version_ ;
      /**
       * <code>uint32 version = 1;</code>
       */
      public int getVersion() {
        return version_;
      }
      /**
       * <code>uint32 version = 1;</code>
       */
      public Builder setVersion(int value) {

        version_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 version = 1;</code>
       */
      public Builder clearVersion() {

        version_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString hashPrevBlock_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes hash_prev_block = 2;</code>
       */
      public com.google.protobuf.ByteString getHashPrevBlock() {
        return hashPrevBlock_;
      }
      /**
       * <code>bytes hash_prev_block = 2;</code>
       */
      public Builder setHashPrevBlock(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        hashPrevBlock_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes hash_prev_block = 2;</code>
       */
      public Builder clearHashPrevBlock() {

        hashPrevBlock_ = getDefaultInstance().getHashPrevBlock();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString hashMerkleRoot_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes hash_merkle_root = 3;</code>
       */
      public com.google.protobuf.ByteString getHashMerkleRoot() {
        return hashMerkleRoot_;
      }
      /**
       * <code>bytes hash_merkle_root = 3;</code>
       */
      public Builder setHashMerkleRoot(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        hashMerkleRoot_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes hash_merkle_root = 3;</code>
       */
      public Builder clearHashMerkleRoot() {

        hashMerkleRoot_ = getDefaultInstance().getHashMerkleRoot();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString hashMerkleState_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes hash_merkle_state = 4;</code>
       */
      public com.google.protobuf.ByteString getHashMerkleState() {
        return hashMerkleState_;
      }
      /**
       * <code>bytes hash_merkle_state = 4;</code>
       */
      public Builder setHashMerkleState(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        hashMerkleState_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes hash_merkle_state = 4;</code>
       */
      public Builder clearHashMerkleState() {

        hashMerkleState_ = getDefaultInstance().getHashMerkleState();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString hashMerkleIncubate_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes hash_merkle_incubate = 5;</code>
       */
      public com.google.protobuf.ByteString getHashMerkleIncubate() {
        return hashMerkleIncubate_;
      }
      /**
       * <code>bytes hash_merkle_incubate = 5;</code>
       */
      public Builder setHashMerkleIncubate(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        hashMerkleIncubate_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes hash_merkle_incubate = 5;</code>
       */
      public Builder clearHashMerkleIncubate() {

        hashMerkleIncubate_ = getDefaultInstance().getHashMerkleIncubate();
        onChanged();
        return this;
      }

      private int height_ ;
      /**
       * <code>uint32 height = 6;</code>
       */
      public int getHeight() {
        return height_;
      }
      /**
       * <code>uint32 height = 6;</code>
       */
      public Builder setHeight(int value) {

        height_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 height = 6;</code>
       */
      public Builder clearHeight() {

        height_ = 0;
        onChanged();
        return this;
      }

      private int createdAt_ ;
      /**
       * <code>uint32 created_at = 7;</code>
       */
      public int getCreatedAt() {
        return createdAt_;
      }
      /**
       * <code>uint32 created_at = 7;</code>
       */
      public Builder setCreatedAt(int value) {

        createdAt_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 created_at = 7;</code>
       */
      public Builder clearCreatedAt() {

        createdAt_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString nBits_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes n_bits = 8;</code>
       */
      public com.google.protobuf.ByteString getNBits() {
        return nBits_;
      }
      /**
       * <code>bytes n_bits = 8;</code>
       */
      public Builder setNBits(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        nBits_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes n_bits = 8;</code>
       */
      public Builder clearNBits() {

        nBits_ = getDefaultInstance().getNBits();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString nonce_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes nonce = 9;</code>
       */
      public com.google.protobuf.ByteString getNonce() {
        return nonce_;
      }
      /**
       * <code>bytes nonce = 9;</code>
       */
      public Builder setNonce(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        nonce_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes nonce = 9;</code>
       */
      public Builder clearNonce() {

        nonce_ = getDefaultInstance().getNonce();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString blockNotice_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes block_notice = 10;</code>
       */
      public com.google.protobuf.ByteString getBlockNotice() {
        return blockNotice_;
      }
      /**
       * <code>bytes block_notice = 10;</code>
       */
      public Builder setBlockNotice(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        blockNotice_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes block_notice = 10;</code>
       */
      public Builder clearBlockNotice() {

        blockNotice_ = getDefaultInstance().getBlockNotice();
        onChanged();
        return this;
      }

      private java.util.List<ProtocolModel.Transaction> body_ =
        java.util.Collections.emptyList();
      private void ensureBodyIsMutable() {
        if (!((bitField0_ & 0x00000400) != 0)) {
          body_ = new java.util.ArrayList<ProtocolModel.Transaction>(body_);
          bitField0_ |= 0x00000400;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          ProtocolModel.Transaction, ProtocolModel.Transaction.Builder, ProtocolModel.TransactionOrBuilder> bodyBuilder_;

      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public java.util.List<ProtocolModel.Transaction> getBodyList() {
        if (bodyBuilder_ == null) {
          return java.util.Collections.unmodifiableList(body_);
        } else {
          return bodyBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public int getBodyCount() {
        if (bodyBuilder_ == null) {
          return body_.size();
        } else {
          return bodyBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public ProtocolModel.Transaction getBody(int index) {
        if (bodyBuilder_ == null) {
          return body_.get(index);
        } else {
          return bodyBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public Builder setBody(
          int index, ProtocolModel.Transaction value) {
        if (bodyBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureBodyIsMutable();
          body_.set(index, value);
          onChanged();
        } else {
          bodyBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public Builder setBody(
          int index, ProtocolModel.Transaction.Builder builderForValue) {
        if (bodyBuilder_ == null) {
          ensureBodyIsMutable();
          body_.set(index, builderForValue.build());
          onChanged();
        } else {
          bodyBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public Builder addBody(ProtocolModel.Transaction value) {
        if (bodyBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureBodyIsMutable();
          body_.add(value);
          onChanged();
        } else {
          bodyBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public Builder addBody(
          int index, ProtocolModel.Transaction value) {
        if (bodyBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureBodyIsMutable();
          body_.add(index, value);
          onChanged();
        } else {
          bodyBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public Builder addBody(
          ProtocolModel.Transaction.Builder builderForValue) {
        if (bodyBuilder_ == null) {
          ensureBodyIsMutable();
          body_.add(builderForValue.build());
          onChanged();
        } else {
          bodyBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public Builder addBody(
          int index, ProtocolModel.Transaction.Builder builderForValue) {
        if (bodyBuilder_ == null) {
          ensureBodyIsMutable();
          body_.add(index, builderForValue.build());
          onChanged();
        } else {
          bodyBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public Builder addAllBody(
          java.lang.Iterable<? extends ProtocolModel.Transaction> values) {
        if (bodyBuilder_ == null) {
          ensureBodyIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, body_);
          onChanged();
        } else {
          bodyBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public Builder clearBody() {
        if (bodyBuilder_ == null) {
          body_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000400);
          onChanged();
        } else {
          bodyBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public Builder removeBody(int index) {
        if (bodyBuilder_ == null) {
          ensureBodyIsMutable();
          body_.remove(index);
          onChanged();
        } else {
          bodyBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public ProtocolModel.Transaction.Builder getBodyBuilder(
          int index) {
        return getBodyFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public ProtocolModel.TransactionOrBuilder getBodyOrBuilder(
          int index) {
        if (bodyBuilder_ == null) {
          return body_.get(index);  } else {
          return bodyBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public java.util.List<? extends ProtocolModel.TransactionOrBuilder>
           getBodyOrBuilderList() {
        if (bodyBuilder_ != null) {
          return bodyBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(body_);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public ProtocolModel.Transaction.Builder addBodyBuilder() {
        return getBodyFieldBuilder().addBuilder(
            ProtocolModel.Transaction.getDefaultInstance());
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public ProtocolModel.Transaction.Builder addBodyBuilder(
          int index) {
        return getBodyFieldBuilder().addBuilder(
            index, ProtocolModel.Transaction.getDefaultInstance());
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Transaction body = 11;</code>
       */
      public java.util.List<ProtocolModel.Transaction.Builder>
           getBodyBuilderList() {
        return getBodyFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          ProtocolModel.Transaction, ProtocolModel.Transaction.Builder, ProtocolModel.TransactionOrBuilder>
          getBodyFieldBuilder() {
        if (bodyBuilder_ == null) {
          bodyBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              ProtocolModel.Transaction, ProtocolModel.Transaction.Builder, ProtocolModel.TransactionOrBuilder>(
                  body_,
                  ((bitField0_ & 0x00000400) != 0),
                  getParentForChildren(),
                  isClean());
          body_ = null;
        }
        return bodyBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.Block)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.Block)
    private static final ProtocolModel.Block DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.Block();
    }

    public static ProtocolModel.Block getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Block>
        PARSER = new com.google.protobuf.AbstractParser<Block>() {
      @java.lang.Override
      public Block parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Block(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Block> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Block> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.Block getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface TransactionOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.Transaction)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 version = 1;</code>
     */
    int getVersion();

    /**
     * <code>bytes hash = 2;</code>
     */
    com.google.protobuf.ByteString getHash();

    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction.Type type = 3;</code>
     */
    int getTypeValue();
    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction.Type type = 3;</code>
     */
    ProtocolModel.Transaction.Type getType();

    /**
     * <code>uint64 nonce = 4;</code>
     */
    long getNonce();

    /**
     * <code>bytes from = 5;</code>
     */
    com.google.protobuf.ByteString getFrom();

    /**
     * <code>uint64 gas_price = 6;</code>
     */
    long getGasPrice();

    /**
     * <code>uint64 amount = 7;</code>
     */
    long getAmount();

    /**
     * <code>bytes to = 8;</code>
     */
    com.google.protobuf.ByteString getTo();

    /**
     * <code>bytes signature = 9;</code>
     */
    com.google.protobuf.ByteString getSignature();

    /**
     * <code>uint32 payloadlen = 10;</code>
     */
    int getPayloadlen();

    /**
     * <code>bytes payload = 11;</code>
     */
    com.google.protobuf.ByteString getPayload();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.Transaction}
   */
  public  static final class Transaction extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.Transaction)
      TransactionOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Transaction.newBuilder() to construct.
    private Transaction(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Transaction() {
      hash_ = com.google.protobuf.ByteString.EMPTY;
      type_ = 0;
      from_ = com.google.protobuf.ByteString.EMPTY;
      to_ = com.google.protobuf.ByteString.EMPTY;
      signature_ = com.google.protobuf.ByteString.EMPTY;
      payload_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Transaction(
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

              version_ = input.readUInt32();
              break;
            }
            case 18: {

              hash_ = input.readBytes();
              break;
            }
            case 24: {
              int rawValue = input.readEnum();

              type_ = rawValue;
              break;
            }
            case 32: {

              nonce_ = input.readUInt64();
              break;
            }
            case 42: {

              from_ = input.readBytes();
              break;
            }
            case 48: {

              gasPrice_ = input.readUInt64();
              break;
            }
            case 56: {

              amount_ = input.readUInt64();
              break;
            }
            case 66: {

              to_ = input.readBytes();
              break;
            }
            case 74: {

              signature_ = input.readBytes();
              break;
            }
            case 80: {

              payloadlen_ = input.readUInt32();
              break;
            }
            case 90: {

              payload_ = input.readBytes();
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Transaction_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Transaction_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.Transaction.class, ProtocolModel.Transaction.Builder.class);
    }

    /**
     * Protobuf enum {@code org.ethereum.protobuf.tcp.Transaction.Type}
     */
    public enum Type
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <pre>
       * coinbase 事务
       * </pre>
       *
       * <code>COINBASE = 0;</code>
       */
      COINBASE(0),
      /**
       * <pre>
       * WDC转账
       * </pre>
       *
       * <code>TRANSFER = 1;</code>
       */
      TRANSFER(1),
      /**
       * <pre>
       * 投票
       * </pre>
       *
       * <code>VOTE = 2;</code>
       */
      VOTE(2),
      /**
       * <pre>
       * 存证
       * </pre>
       *
       * <code>DEPOSIT = 3;</code>
       */
      DEPOSIT(3),
      /**
       * <pre>
       * 转账 多签到多签
       * </pre>
       *
       * <code>TRANSFER_MULTISIG_MULTISIG = 4;</code>
       */
      TRANSFER_MULTISIG_MULTISIG(4),
      /**
       * <pre>
       * 转账 多签到普通
       * </pre>
       *
       * <code>TRANSFER_MULTISIG_NORMAL = 5;</code>
       */
      TRANSFER_MULTISIG_NORMAL(5),
      /**
       * <pre>
       * 转账 普通到多签
       * </pre>
       *
       * <code>TRANSFER_NORMAL_MULTISIG = 6;</code>
       */
      TRANSFER_NORMAL_MULTISIG(6),
      /**
       * <pre>
       * 资产定义
       * </pre>
       *
       * <code>ASSET_DEFINE = 7;</code>
       */
      ASSET_DEFINE(7),
      /**
       * <pre>
       * 原子交换
       * </pre>
       *
       * <code>ATOMIC_EXCHANGE = 8;</code>
       */
      ATOMIC_EXCHANGE(8),
      /**
       * <pre>
       * 申请孵化
       * </pre>
       *
       * <code>INCUBATE = 9;</code>
       */
      INCUBATE(9),
      /**
       * <pre>
       * 获取利息收益
       * </pre>
       *
       * <code>EXTRACT_INTEREST = 10;</code>
       */
      EXTRACT_INTEREST(10),
      /**
       * <pre>
       * 获取分享收益
       * </pre>
       *
       * <code>EXTRACT_SHARING_PROFIT = 11;</code>
       */
      EXTRACT_SHARING_PROFIT(11),
      /**
       * <pre>
       * 停止孵化
       * </pre>
       *
       * <code>TERMINATE_INCUBATE = 12;</code>
       */
      TERMINATE_INCUBATE(12),
      UNRECOGNIZED(-1),
      ;

      /**
       * <pre>
       * coinbase 事务
       * </pre>
       *
       * <code>COINBASE = 0;</code>
       */
      public static final int COINBASE_VALUE = 0;
      /**
       * <pre>
       * WDC转账
       * </pre>
       *
       * <code>TRANSFER = 1;</code>
       */
      public static final int TRANSFER_VALUE = 1;
      /**
       * <pre>
       * 投票
       * </pre>
       *
       * <code>VOTE = 2;</code>
       */
      public static final int VOTE_VALUE = 2;
      /**
       * <pre>
       * 存证
       * </pre>
       *
       * <code>DEPOSIT = 3;</code>
       */
      public static final int DEPOSIT_VALUE = 3;
      /**
       * <pre>
       * 转账 多签到多签
       * </pre>
       *
       * <code>TRANSFER_MULTISIG_MULTISIG = 4;</code>
       */
      public static final int TRANSFER_MULTISIG_MULTISIG_VALUE = 4;
      /**
       * <pre>
       * 转账 多签到普通
       * </pre>
       *
       * <code>TRANSFER_MULTISIG_NORMAL = 5;</code>
       */
      public static final int TRANSFER_MULTISIG_NORMAL_VALUE = 5;
      /**
       * <pre>
       * 转账 普通到多签
       * </pre>
       *
       * <code>TRANSFER_NORMAL_MULTISIG = 6;</code>
       */
      public static final int TRANSFER_NORMAL_MULTISIG_VALUE = 6;
      /**
       * <pre>
       * 资产定义
       * </pre>
       *
       * <code>ASSET_DEFINE = 7;</code>
       */
      public static final int ASSET_DEFINE_VALUE = 7;
      /**
       * <pre>
       * 原子交换
       * </pre>
       *
       * <code>ATOMIC_EXCHANGE = 8;</code>
       */
      public static final int ATOMIC_EXCHANGE_VALUE = 8;
      /**
       * <pre>
       * 申请孵化
       * </pre>
       *
       * <code>INCUBATE = 9;</code>
       */
      public static final int INCUBATE_VALUE = 9;
      /**
       * <pre>
       * 获取利息收益
       * </pre>
       *
       * <code>EXTRACT_INTEREST = 10;</code>
       */
      public static final int EXTRACT_INTEREST_VALUE = 10;
      /**
       * <pre>
       * 获取分享收益
       * </pre>
       *
       * <code>EXTRACT_SHARING_PROFIT = 11;</code>
       */
      public static final int EXTRACT_SHARING_PROFIT_VALUE = 11;
      /**
       * <pre>
       * 停止孵化
       * </pre>
       *
       * <code>TERMINATE_INCUBATE = 12;</code>
       */
      public static final int TERMINATE_INCUBATE_VALUE = 12;


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
      public static Type valueOf(int value) {
        return forNumber(value);
      }

      public static Type forNumber(int value) {
        switch (value) {
          case 0: return COINBASE;
          case 1: return TRANSFER;
          case 2: return VOTE;
          case 3: return DEPOSIT;
          case 4: return TRANSFER_MULTISIG_MULTISIG;
          case 5: return TRANSFER_MULTISIG_NORMAL;
          case 6: return TRANSFER_NORMAL_MULTISIG;
          case 7: return ASSET_DEFINE;
          case 8: return ATOMIC_EXCHANGE;
          case 9: return INCUBATE;
          case 10: return EXTRACT_INTEREST;
          case 11: return EXTRACT_SHARING_PROFIT;
          case 12: return TERMINATE_INCUBATE;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<Type>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          Type> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<Type>() {
              public Type findValueByNumber(int number) {
                return Type.forNumber(number);
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
        return ProtocolModel.Transaction.getDescriptor().getEnumTypes().get(0);
      }

      private static final Type[] VALUES = values();

      public static Type valueOf(
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

      private Type(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:org.ethereum.protobuf.tcp.Transaction.Type)
    }

    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_;
    /**
     * <code>uint32 version = 1;</code>
     */
    public int getVersion() {
      return version_;
    }

    public static final int HASH_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString hash_;
    /**
     * <code>bytes hash = 2;</code>
     */
    public com.google.protobuf.ByteString getHash() {
      return hash_;
    }

    public static final int TYPE_FIELD_NUMBER = 3;
    private int type_;
    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction.Type type = 3;</code>
     */
    public int getTypeValue() {
      return type_;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.Transaction.Type type = 3;</code>
     */
    public ProtocolModel.Transaction.Type getType() {
      @SuppressWarnings("deprecation")
      ProtocolModel.Transaction.Type result = ProtocolModel.Transaction.Type.valueOf(type_);
      return result == null ? ProtocolModel.Transaction.Type.UNRECOGNIZED : result;
    }

    public static final int NONCE_FIELD_NUMBER = 4;
    private long nonce_;
    /**
     * <code>uint64 nonce = 4;</code>
     */
    public long getNonce() {
      return nonce_;
    }

    public static final int FROM_FIELD_NUMBER = 5;
    private com.google.protobuf.ByteString from_;
    /**
     * <code>bytes from = 5;</code>
     */
    public com.google.protobuf.ByteString getFrom() {
      return from_;
    }

    public static final int GAS_PRICE_FIELD_NUMBER = 6;
    private long gasPrice_;
    /**
     * <code>uint64 gas_price = 6;</code>
     */
    public long getGasPrice() {
      return gasPrice_;
    }

    public static final int AMOUNT_FIELD_NUMBER = 7;
    private long amount_;
    /**
     * <code>uint64 amount = 7;</code>
     */
    public long getAmount() {
      return amount_;
    }

    public static final int TO_FIELD_NUMBER = 8;
    private com.google.protobuf.ByteString to_;
    /**
     * <code>bytes to = 8;</code>
     */
    public com.google.protobuf.ByteString getTo() {
      return to_;
    }

    public static final int SIGNATURE_FIELD_NUMBER = 9;
    private com.google.protobuf.ByteString signature_;
    /**
     * <code>bytes signature = 9;</code>
     */
    public com.google.protobuf.ByteString getSignature() {
      return signature_;
    }

    public static final int PAYLOADLEN_FIELD_NUMBER = 10;
    private int payloadlen_;
    /**
     * <code>uint32 payloadlen = 10;</code>
     */
    public int getPayloadlen() {
      return payloadlen_;
    }

    public static final int PAYLOAD_FIELD_NUMBER = 11;
    private com.google.protobuf.ByteString payload_;
    /**
     * <code>bytes payload = 11;</code>
     */
    public com.google.protobuf.ByteString getPayload() {
      return payload_;
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
      if (version_ != 0) {
        output.writeUInt32(1, version_);
      }
      if (!hash_.isEmpty()) {
        output.writeBytes(2, hash_);
      }
      if (type_ != ProtocolModel.Transaction.Type.COINBASE.getNumber()) {
        output.writeEnum(3, type_);
      }
      if (nonce_ != 0L) {
        output.writeUInt64(4, nonce_);
      }
      if (!from_.isEmpty()) {
        output.writeBytes(5, from_);
      }
      if (gasPrice_ != 0L) {
        output.writeUInt64(6, gasPrice_);
      }
      if (amount_ != 0L) {
        output.writeUInt64(7, amount_);
      }
      if (!to_.isEmpty()) {
        output.writeBytes(8, to_);
      }
      if (!signature_.isEmpty()) {
        output.writeBytes(9, signature_);
      }
      if (payloadlen_ != 0) {
        output.writeUInt32(10, payloadlen_);
      }
      if (!payload_.isEmpty()) {
        output.writeBytes(11, payload_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (version_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, version_);
      }
      if (!hash_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, hash_);
      }
      if (type_ != ProtocolModel.Transaction.Type.COINBASE.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(3, type_);
      }
      if (nonce_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(4, nonce_);
      }
      if (!from_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(5, from_);
      }
      if (gasPrice_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(6, gasPrice_);
      }
      if (amount_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(7, amount_);
      }
      if (!to_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(8, to_);
      }
      if (!signature_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(9, signature_);
      }
      if (payloadlen_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(10, payloadlen_);
      }
      if (!payload_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(11, payload_);
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
      if (!(obj instanceof ProtocolModel.Transaction)) {
        return super.equals(obj);
      }
      ProtocolModel.Transaction other = (ProtocolModel.Transaction) obj;

      if (getVersion()
          != other.getVersion()) return false;
      if (!getHash()
          .equals(other.getHash())) return false;
      if (type_ != other.type_) return false;
      if (getNonce()
          != other.getNonce()) return false;
      if (!getFrom()
          .equals(other.getFrom())) return false;
      if (getGasPrice()
          != other.getGasPrice()) return false;
      if (getAmount()
          != other.getAmount()) return false;
      if (!getTo()
          .equals(other.getTo())) return false;
      if (!getSignature()
          .equals(other.getSignature())) return false;
      if (getPayloadlen()
          != other.getPayloadlen()) return false;
      if (!getPayload()
          .equals(other.getPayload())) return false;
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
      hash = (37 * hash) + VERSION_FIELD_NUMBER;
      hash = (53 * hash) + getVersion();
      hash = (37 * hash) + HASH_FIELD_NUMBER;
      hash = (53 * hash) + getHash().hashCode();
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + type_;
      hash = (37 * hash) + NONCE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getNonce());
      hash = (37 * hash) + FROM_FIELD_NUMBER;
      hash = (53 * hash) + getFrom().hashCode();
      hash = (37 * hash) + GAS_PRICE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getGasPrice());
      hash = (37 * hash) + AMOUNT_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getAmount());
      hash = (37 * hash) + TO_FIELD_NUMBER;
      hash = (53 * hash) + getTo().hashCode();
      hash = (37 * hash) + SIGNATURE_FIELD_NUMBER;
      hash = (53 * hash) + getSignature().hashCode();
      hash = (37 * hash) + PAYLOADLEN_FIELD_NUMBER;
      hash = (53 * hash) + getPayloadlen();
      hash = (37 * hash) + PAYLOAD_FIELD_NUMBER;
      hash = (53 * hash) + getPayload().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.Transaction parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.Transaction parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.Transaction parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.Transaction parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.Transaction parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.Transaction parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.Transaction parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.Transaction parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.Transaction parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.Transaction parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.Transaction parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.Transaction parseFrom(
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
    public static Builder newBuilder(ProtocolModel.Transaction prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.Transaction}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.Transaction)
        ProtocolModel.TransactionOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Transaction_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Transaction_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.Transaction.class, ProtocolModel.Transaction.Builder.class);
      }

      // Construct using ProtocolModel.Transaction.newBuilder()
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
        version_ = 0;

        hash_ = com.google.protobuf.ByteString.EMPTY;

        type_ = 0;

        nonce_ = 0L;

        from_ = com.google.protobuf.ByteString.EMPTY;

        gasPrice_ = 0L;

        amount_ = 0L;

        to_ = com.google.protobuf.ByteString.EMPTY;

        signature_ = com.google.protobuf.ByteString.EMPTY;

        payloadlen_ = 0;

        payload_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_Transaction_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.Transaction getDefaultInstanceForType() {
        return ProtocolModel.Transaction.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.Transaction build() {
        ProtocolModel.Transaction result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.Transaction buildPartial() {
        ProtocolModel.Transaction result = new ProtocolModel.Transaction(this);
        result.version_ = version_;
        result.hash_ = hash_;
        result.type_ = type_;
        result.nonce_ = nonce_;
        result.from_ = from_;
        result.gasPrice_ = gasPrice_;
        result.amount_ = amount_;
        result.to_ = to_;
        result.signature_ = signature_;
        result.payloadlen_ = payloadlen_;
        result.payload_ = payload_;
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
        if (other instanceof ProtocolModel.Transaction) {
          return mergeFrom((ProtocolModel.Transaction)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.Transaction other) {
        if (other == ProtocolModel.Transaction.getDefaultInstance()) return this;
        if (other.getVersion() != 0) {
          setVersion(other.getVersion());
        }
        if (other.getHash() != com.google.protobuf.ByteString.EMPTY) {
          setHash(other.getHash());
        }
        if (other.type_ != 0) {
          setTypeValue(other.getTypeValue());
        }
        if (other.getNonce() != 0L) {
          setNonce(other.getNonce());
        }
        if (other.getFrom() != com.google.protobuf.ByteString.EMPTY) {
          setFrom(other.getFrom());
        }
        if (other.getGasPrice() != 0L) {
          setGasPrice(other.getGasPrice());
        }
        if (other.getAmount() != 0L) {
          setAmount(other.getAmount());
        }
        if (other.getTo() != com.google.protobuf.ByteString.EMPTY) {
          setTo(other.getTo());
        }
        if (other.getSignature() != com.google.protobuf.ByteString.EMPTY) {
          setSignature(other.getSignature());
        }
        if (other.getPayloadlen() != 0) {
          setPayloadlen(other.getPayloadlen());
        }
        if (other.getPayload() != com.google.protobuf.ByteString.EMPTY) {
          setPayload(other.getPayload());
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
        ProtocolModel.Transaction parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.Transaction) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int version_ ;
      /**
       * <code>uint32 version = 1;</code>
       */
      public int getVersion() {
        return version_;
      }
      /**
       * <code>uint32 version = 1;</code>
       */
      public Builder setVersion(int value) {

        version_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 version = 1;</code>
       */
      public Builder clearVersion() {

        version_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString hash_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes hash = 2;</code>
       */
      public com.google.protobuf.ByteString getHash() {
        return hash_;
      }
      /**
       * <code>bytes hash = 2;</code>
       */
      public Builder setHash(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        hash_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes hash = 2;</code>
       */
      public Builder clearHash() {

        hash_ = getDefaultInstance().getHash();
        onChanged();
        return this;
      }

      private int type_ = 0;
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction.Type type = 3;</code>
       */
      public int getTypeValue() {
        return type_;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction.Type type = 3;</code>
       */
      public Builder setTypeValue(int value) {
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction.Type type = 3;</code>
       */
      public ProtocolModel.Transaction.Type getType() {
        @SuppressWarnings("deprecation")
        ProtocolModel.Transaction.Type result = ProtocolModel.Transaction.Type.valueOf(type_);
        return result == null ? ProtocolModel.Transaction.Type.UNRECOGNIZED : result;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction.Type type = 3;</code>
       */
      public Builder setType(ProtocolModel.Transaction.Type value) {
        if (value == null) {
          throw new NullPointerException();
        }

        type_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.Transaction.Type type = 3;</code>
       */
      public Builder clearType() {

        type_ = 0;
        onChanged();
        return this;
      }

      private long nonce_ ;
      /**
       * <code>uint64 nonce = 4;</code>
       */
      public long getNonce() {
        return nonce_;
      }
      /**
       * <code>uint64 nonce = 4;</code>
       */
      public Builder setNonce(long value) {

        nonce_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint64 nonce = 4;</code>
       */
      public Builder clearNonce() {

        nonce_ = 0L;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString from_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes from = 5;</code>
       */
      public com.google.protobuf.ByteString getFrom() {
        return from_;
      }
      /**
       * <code>bytes from = 5;</code>
       */
      public Builder setFrom(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        from_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes from = 5;</code>
       */
      public Builder clearFrom() {

        from_ = getDefaultInstance().getFrom();
        onChanged();
        return this;
      }

      private long gasPrice_ ;
      /**
       * <code>uint64 gas_price = 6;</code>
       */
      public long getGasPrice() {
        return gasPrice_;
      }
      /**
       * <code>uint64 gas_price = 6;</code>
       */
      public Builder setGasPrice(long value) {

        gasPrice_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint64 gas_price = 6;</code>
       */
      public Builder clearGasPrice() {

        gasPrice_ = 0L;
        onChanged();
        return this;
      }

      private long amount_ ;
      /**
       * <code>uint64 amount = 7;</code>
       */
      public long getAmount() {
        return amount_;
      }
      /**
       * <code>uint64 amount = 7;</code>
       */
      public Builder setAmount(long value) {

        amount_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint64 amount = 7;</code>
       */
      public Builder clearAmount() {

        amount_ = 0L;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString to_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes to = 8;</code>
       */
      public com.google.protobuf.ByteString getTo() {
        return to_;
      }
      /**
       * <code>bytes to = 8;</code>
       */
      public Builder setTo(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        to_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes to = 8;</code>
       */
      public Builder clearTo() {

        to_ = getDefaultInstance().getTo();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString signature_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes signature = 9;</code>
       */
      public com.google.protobuf.ByteString getSignature() {
        return signature_;
      }
      /**
       * <code>bytes signature = 9;</code>
       */
      public Builder setSignature(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        signature_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes signature = 9;</code>
       */
      public Builder clearSignature() {

        signature_ = getDefaultInstance().getSignature();
        onChanged();
        return this;
      }

      private int payloadlen_ ;
      /**
       * <code>uint32 payloadlen = 10;</code>
       */
      public int getPayloadlen() {
        return payloadlen_;
      }
      /**
       * <code>uint32 payloadlen = 10;</code>
       */
      public Builder setPayloadlen(int value) {

        payloadlen_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 payloadlen = 10;</code>
       */
      public Builder clearPayloadlen() {

        payloadlen_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString payload_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes payload = 11;</code>
       */
      public com.google.protobuf.ByteString getPayload() {
        return payload_;
      }
      /**
       * <code>bytes payload = 11;</code>
       */
      public Builder setPayload(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        payload_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes payload = 11;</code>
       */
      public Builder clearPayload() {

        payload_ = getDefaultInstance().getPayload();
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.Transaction)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.Transaction)
    private static final ProtocolModel.Transaction DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.Transaction();
    }

    public static ProtocolModel.Transaction getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Transaction>
        PARSER = new com.google.protobuf.AbstractParser<Transaction>() {
      @java.lang.Override
      public Transaction parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Transaction(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Transaction> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Transaction> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.Transaction getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface StatusMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.StatusMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 version = 1;</code>
     */
    int getVersion();

    /**
     * <code>uint32 current_height = 2;</code>
     */
    int getCurrentHeight();

    /**
     * <code>bytes current_block_hash = 3;</code>
     */
    com.google.protobuf.ByteString getCurrentBlockHash();

    /**
     * <pre>
     * big-endian unsigned big integer
     * </pre>
     *
     * <code>uint32 total_weight = 4;</code>
     */
    int getTotalWeight();

    /**
     * <pre>
     * genesis block hash
     * </pre>
     *
     * <code>bytes genesis_hash = 5;</code>
     */
    com.google.protobuf.ByteString getGenesisHash();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.StatusMessage}
   */
  public  static final class StatusMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.StatusMessage)
      StatusMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use StatusMessage.newBuilder() to construct.
    private StatusMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private StatusMessage() {
      currentBlockHash_ = com.google.protobuf.ByteString.EMPTY;
      genesisHash_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private StatusMessage(
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

              version_ = input.readUInt32();
              break;
            }
            case 16: {

              currentHeight_ = input.readUInt32();
              break;
            }
            case 26: {

              currentBlockHash_ = input.readBytes();
              break;
            }
            case 32: {

              totalWeight_ = input.readUInt32();
              break;
            }
            case 42: {

              genesisHash_ = input.readBytes();
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_StatusMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_StatusMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.StatusMessage.class, ProtocolModel.StatusMessage.Builder.class);
    }

    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_;
    /**
     * <code>uint32 version = 1;</code>
     */
    public int getVersion() {
      return version_;
    }

    public static final int CURRENT_HEIGHT_FIELD_NUMBER = 2;
    private int currentHeight_;
    /**
     * <code>uint32 current_height = 2;</code>
     */
    public int getCurrentHeight() {
      return currentHeight_;
    }

    public static final int CURRENT_BLOCK_HASH_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString currentBlockHash_;
    /**
     * <code>bytes current_block_hash = 3;</code>
     */
    public com.google.protobuf.ByteString getCurrentBlockHash() {
      return currentBlockHash_;
    }

    public static final int TOTAL_WEIGHT_FIELD_NUMBER = 4;
    private int totalWeight_;
    /**
     * <pre>
     * big-endian unsigned big integer
     * </pre>
     *
     * <code>uint32 total_weight = 4;</code>
     */
    public int getTotalWeight() {
      return totalWeight_;
    }

    public static final int GENESIS_HASH_FIELD_NUMBER = 5;
    private com.google.protobuf.ByteString genesisHash_;
    /**
     * <pre>
     * genesis block hash
     * </pre>
     *
     * <code>bytes genesis_hash = 5;</code>
     */
    public com.google.protobuf.ByteString getGenesisHash() {
      return genesisHash_;
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
      if (version_ != 0) {
        output.writeUInt32(1, version_);
      }
      if (currentHeight_ != 0) {
        output.writeUInt32(2, currentHeight_);
      }
      if (!currentBlockHash_.isEmpty()) {
        output.writeBytes(3, currentBlockHash_);
      }
      if (totalWeight_ != 0) {
        output.writeUInt32(4, totalWeight_);
      }
      if (!genesisHash_.isEmpty()) {
        output.writeBytes(5, genesisHash_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (version_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, version_);
      }
      if (currentHeight_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(2, currentHeight_);
      }
      if (!currentBlockHash_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, currentBlockHash_);
      }
      if (totalWeight_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(4, totalWeight_);
      }
      if (!genesisHash_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(5, genesisHash_);
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
      if (!(obj instanceof ProtocolModel.StatusMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.StatusMessage other = (ProtocolModel.StatusMessage) obj;

      if (getVersion()
          != other.getVersion()) return false;
      if (getCurrentHeight()
          != other.getCurrentHeight()) return false;
      if (!getCurrentBlockHash()
          .equals(other.getCurrentBlockHash())) return false;
      if (getTotalWeight()
          != other.getTotalWeight()) return false;
      if (!getGenesisHash()
          .equals(other.getGenesisHash())) return false;
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
      hash = (37 * hash) + VERSION_FIELD_NUMBER;
      hash = (53 * hash) + getVersion();
      hash = (37 * hash) + CURRENT_HEIGHT_FIELD_NUMBER;
      hash = (53 * hash) + getCurrentHeight();
      hash = (37 * hash) + CURRENT_BLOCK_HASH_FIELD_NUMBER;
      hash = (53 * hash) + getCurrentBlockHash().hashCode();
      hash = (37 * hash) + TOTAL_WEIGHT_FIELD_NUMBER;
      hash = (53 * hash) + getTotalWeight();
      hash = (37 * hash) + GENESIS_HASH_FIELD_NUMBER;
      hash = (53 * hash) + getGenesisHash().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.StatusMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.StatusMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.StatusMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.StatusMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.StatusMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.StatusMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.StatusMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.StatusMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.StatusMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.StatusMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.StatusMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.StatusMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.StatusMessage prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.StatusMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.StatusMessage)
        ProtocolModel.StatusMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_StatusMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_StatusMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.StatusMessage.class, ProtocolModel.StatusMessage.Builder.class);
      }

      // Construct using ProtocolModel.StatusMessage.newBuilder()
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
        version_ = 0;

        currentHeight_ = 0;

        currentBlockHash_ = com.google.protobuf.ByteString.EMPTY;

        totalWeight_ = 0;

        genesisHash_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_StatusMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.StatusMessage getDefaultInstanceForType() {
        return ProtocolModel.StatusMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.StatusMessage build() {
        ProtocolModel.StatusMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.StatusMessage buildPartial() {
        ProtocolModel.StatusMessage result = new ProtocolModel.StatusMessage(this);
        result.version_ = version_;
        result.currentHeight_ = currentHeight_;
        result.currentBlockHash_ = currentBlockHash_;
        result.totalWeight_ = totalWeight_;
        result.genesisHash_ = genesisHash_;
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
        if (other instanceof ProtocolModel.StatusMessage) {
          return mergeFrom((ProtocolModel.StatusMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.StatusMessage other) {
        if (other == ProtocolModel.StatusMessage.getDefaultInstance()) return this;
        if (other.getVersion() != 0) {
          setVersion(other.getVersion());
        }
        if (other.getCurrentHeight() != 0) {
          setCurrentHeight(other.getCurrentHeight());
        }
        if (other.getCurrentBlockHash() != com.google.protobuf.ByteString.EMPTY) {
          setCurrentBlockHash(other.getCurrentBlockHash());
        }
        if (other.getTotalWeight() != 0) {
          setTotalWeight(other.getTotalWeight());
        }
        if (other.getGenesisHash() != com.google.protobuf.ByteString.EMPTY) {
          setGenesisHash(other.getGenesisHash());
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
        ProtocolModel.StatusMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.StatusMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int version_ ;
      /**
       * <code>uint32 version = 1;</code>
       */
      public int getVersion() {
        return version_;
      }
      /**
       * <code>uint32 version = 1;</code>
       */
      public Builder setVersion(int value) {

        version_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 version = 1;</code>
       */
      public Builder clearVersion() {

        version_ = 0;
        onChanged();
        return this;
      }

      private int currentHeight_ ;
      /**
       * <code>uint32 current_height = 2;</code>
       */
      public int getCurrentHeight() {
        return currentHeight_;
      }
      /**
       * <code>uint32 current_height = 2;</code>
       */
      public Builder setCurrentHeight(int value) {

        currentHeight_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 current_height = 2;</code>
       */
      public Builder clearCurrentHeight() {

        currentHeight_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString currentBlockHash_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes current_block_hash = 3;</code>
       */
      public com.google.protobuf.ByteString getCurrentBlockHash() {
        return currentBlockHash_;
      }
      /**
       * <code>bytes current_block_hash = 3;</code>
       */
      public Builder setCurrentBlockHash(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        currentBlockHash_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes current_block_hash = 3;</code>
       */
      public Builder clearCurrentBlockHash() {

        currentBlockHash_ = getDefaultInstance().getCurrentBlockHash();
        onChanged();
        return this;
      }

      private int totalWeight_ ;
      /**
       * <pre>
       * big-endian unsigned big integer
       * </pre>
       *
       * <code>uint32 total_weight = 4;</code>
       */
      public int getTotalWeight() {
        return totalWeight_;
      }
      /**
       * <pre>
       * big-endian unsigned big integer
       * </pre>
       *
       * <code>uint32 total_weight = 4;</code>
       */
      public Builder setTotalWeight(int value) {

        totalWeight_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * big-endian unsigned big integer
       * </pre>
       *
       * <code>uint32 total_weight = 4;</code>
       */
      public Builder clearTotalWeight() {

        totalWeight_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString genesisHash_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <pre>
       * genesis block hash
       * </pre>
       *
       * <code>bytes genesis_hash = 5;</code>
       */
      public com.google.protobuf.ByteString getGenesisHash() {
        return genesisHash_;
      }
      /**
       * <pre>
       * genesis block hash
       * </pre>
       *
       * <code>bytes genesis_hash = 5;</code>
       */
      public Builder setGenesisHash(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        genesisHash_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * genesis block hash
       * </pre>
       *
       * <code>bytes genesis_hash = 5;</code>
       */
      public Builder clearGenesisHash() {

        genesisHash_ = getDefaultInstance().getGenesisHash();
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.StatusMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.StatusMessage)
    private static final ProtocolModel.StatusMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.StatusMessage();
    }

    public static ProtocolModel.StatusMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<StatusMessage>
        PARSER = new com.google.protobuf.AbstractParser<StatusMessage>() {
      @java.lang.Override
      public StatusMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new StatusMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<StatusMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<StatusMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.StatusMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface GetHeadersMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.GetHeadersMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * number of block locator hash entries
     * </pre>
     *
     * <code>uint32 hash_count = 1;</code>
     */
    int getHashCount();

    /**
     * <code>bytes block_locator_hash = 2;</code>
     */
    com.google.protobuf.ByteString getBlockLocatorHash();

    /**
     * <code>bytes hash_stop = 3;</code>
     */
    com.google.protobuf.ByteString getHashStop();
  }
  /**
   * <pre>
   * fetch canonical headers
   * </pre>
   *
   * Protobuf type {@code org.ethereum.protobuf.tcp.GetHeadersMessage}
   */
  public  static final class GetHeadersMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.GetHeadersMessage)
      GetHeadersMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use GetHeadersMessage.newBuilder() to construct.
    private GetHeadersMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private GetHeadersMessage() {
      blockLocatorHash_ = com.google.protobuf.ByteString.EMPTY;
      hashStop_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private GetHeadersMessage(
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

              hashCount_ = input.readUInt32();
              break;
            }
            case 18: {

              blockLocatorHash_ = input.readBytes();
              break;
            }
            case 26: {

              hashStop_ = input.readBytes();
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.GetHeadersMessage.class, ProtocolModel.GetHeadersMessage.Builder.class);
    }

    public static final int HASH_COUNT_FIELD_NUMBER = 1;
    private int hashCount_;
    /**
     * <pre>
     * number of block locator hash entries
     * </pre>
     *
     * <code>uint32 hash_count = 1;</code>
     */
    public int getHashCount() {
      return hashCount_;
    }

    public static final int BLOCK_LOCATOR_HASH_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString blockLocatorHash_;
    /**
     * <code>bytes block_locator_hash = 2;</code>
     */
    public com.google.protobuf.ByteString getBlockLocatorHash() {
      return blockLocatorHash_;
    }

    public static final int HASH_STOP_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString hashStop_;
    /**
     * <code>bytes hash_stop = 3;</code>
     */
    public com.google.protobuf.ByteString getHashStop() {
      return hashStop_;
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
      if (hashCount_ != 0) {
        output.writeUInt32(1, hashCount_);
      }
      if (!blockLocatorHash_.isEmpty()) {
        output.writeBytes(2, blockLocatorHash_);
      }
      if (!hashStop_.isEmpty()) {
        output.writeBytes(3, hashStop_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (hashCount_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, hashCount_);
      }
      if (!blockLocatorHash_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, blockLocatorHash_);
      }
      if (!hashStop_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, hashStop_);
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
      if (!(obj instanceof ProtocolModel.GetHeadersMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.GetHeadersMessage other = (ProtocolModel.GetHeadersMessage) obj;

      if (getHashCount()
          != other.getHashCount()) return false;
      if (!getBlockLocatorHash()
          .equals(other.getBlockLocatorHash())) return false;
      if (!getHashStop()
          .equals(other.getHashStop())) return false;
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
      hash = (37 * hash) + HASH_COUNT_FIELD_NUMBER;
      hash = (53 * hash) + getHashCount();
      hash = (37 * hash) + BLOCK_LOCATOR_HASH_FIELD_NUMBER;
      hash = (53 * hash) + getBlockLocatorHash().hashCode();
      hash = (37 * hash) + HASH_STOP_FIELD_NUMBER;
      hash = (53 * hash) + getHashStop().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.GetHeadersMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.GetHeadersMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.GetHeadersMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.GetHeadersMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.GetHeadersMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.GetHeadersMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.GetHeadersMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.GetHeadersMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.GetHeadersMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.GetHeadersMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.GetHeadersMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.GetHeadersMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.GetHeadersMessage prototype) {
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
     * <pre>
     * fetch canonical headers
     * </pre>
     *
     * Protobuf type {@code org.ethereum.protobuf.tcp.GetHeadersMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.GetHeadersMessage)
        ProtocolModel.GetHeadersMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.GetHeadersMessage.class, ProtocolModel.GetHeadersMessage.Builder.class);
      }

      // Construct using ProtocolModel.GetHeadersMessage.newBuilder()
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
        hashCount_ = 0;

        blockLocatorHash_ = com.google.protobuf.ByteString.EMPTY;

        hashStop_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.GetHeadersMessage getDefaultInstanceForType() {
        return ProtocolModel.GetHeadersMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.GetHeadersMessage build() {
        ProtocolModel.GetHeadersMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.GetHeadersMessage buildPartial() {
        ProtocolModel.GetHeadersMessage result = new ProtocolModel.GetHeadersMessage(this);
        result.hashCount_ = hashCount_;
        result.blockLocatorHash_ = blockLocatorHash_;
        result.hashStop_ = hashStop_;
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
        if (other instanceof ProtocolModel.GetHeadersMessage) {
          return mergeFrom((ProtocolModel.GetHeadersMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.GetHeadersMessage other) {
        if (other == ProtocolModel.GetHeadersMessage.getDefaultInstance()) return this;
        if (other.getHashCount() != 0) {
          setHashCount(other.getHashCount());
        }
        if (other.getBlockLocatorHash() != com.google.protobuf.ByteString.EMPTY) {
          setBlockLocatorHash(other.getBlockLocatorHash());
        }
        if (other.getHashStop() != com.google.protobuf.ByteString.EMPTY) {
          setHashStop(other.getHashStop());
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
        ProtocolModel.GetHeadersMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.GetHeadersMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int hashCount_ ;
      /**
       * <pre>
       * number of block locator hash entries
       * </pre>
       *
       * <code>uint32 hash_count = 1;</code>
       */
      public int getHashCount() {
        return hashCount_;
      }
      /**
       * <pre>
       * number of block locator hash entries
       * </pre>
       *
       * <code>uint32 hash_count = 1;</code>
       */
      public Builder setHashCount(int value) {

        hashCount_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * number of block locator hash entries
       * </pre>
       *
       * <code>uint32 hash_count = 1;</code>
       */
      public Builder clearHashCount() {

        hashCount_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString blockLocatorHash_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes block_locator_hash = 2;</code>
       */
      public com.google.protobuf.ByteString getBlockLocatorHash() {
        return blockLocatorHash_;
      }
      /**
       * <code>bytes block_locator_hash = 2;</code>
       */
      public Builder setBlockLocatorHash(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        blockLocatorHash_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes block_locator_hash = 2;</code>
       */
      public Builder clearBlockLocatorHash() {

        blockLocatorHash_ = getDefaultInstance().getBlockLocatorHash();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString hashStop_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes hash_stop = 3;</code>
       */
      public com.google.protobuf.ByteString getHashStop() {
        return hashStop_;
      }
      /**
       * <code>bytes hash_stop = 3;</code>
       */
      public Builder setHashStop(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }

        hashStop_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes hash_stop = 3;</code>
       */
      public Builder clearHashStop() {

        hashStop_ = getDefaultInstance().getHashStop();
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.GetHeadersMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.GetHeadersMessage)
    private static final ProtocolModel.GetHeadersMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.GetHeadersMessage();
    }

    public static ProtocolModel.GetHeadersMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<GetHeadersMessage>
        PARSER = new com.google.protobuf.AbstractParser<GetHeadersMessage>() {
      @java.lang.Override
      public GetHeadersMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new GetHeadersMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<GetHeadersMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<GetHeadersMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.GetHeadersMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface HeadersMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.HeadersMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 count = 1;</code>
     */
    int getCount();

    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    java.util.List<ProtocolModel.Block>
        getHeadersList();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    ProtocolModel.Block getHeaders(int index);
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    int getHeadersCount();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    java.util.List<? extends ProtocolModel.BlockOrBuilder>
        getHeadersOrBuilderList();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    ProtocolModel.BlockOrBuilder getHeadersOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.HeadersMessage}
   */
  public  static final class HeadersMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.HeadersMessage)
      HeadersMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use HeadersMessage.newBuilder() to construct.
    private HeadersMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private HeadersMessage() {
      headers_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private HeadersMessage(
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

              count_ = input.readUInt32();
              break;
            }
            case 18: {
              if (!((mutable_bitField0_ & 0x00000002) != 0)) {
                headers_ = new java.util.ArrayList<ProtocolModel.Block>();
                mutable_bitField0_ |= 0x00000002;
              }
              headers_.add(
                  input.readMessage(ProtocolModel.Block.parser(), extensionRegistry));
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
        if (((mutable_bitField0_ & 0x00000002) != 0)) {
          headers_ = java.util.Collections.unmodifiableList(headers_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_HeadersMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_HeadersMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.HeadersMessage.class, ProtocolModel.HeadersMessage.Builder.class);
    }

    private int bitField0_;
    public static final int COUNT_FIELD_NUMBER = 1;
    private int count_;
    /**
     * <code>uint32 count = 1;</code>
     */
    public int getCount() {
      return count_;
    }

    public static final int HEADERS_FIELD_NUMBER = 2;
    private java.util.List<ProtocolModel.Block> headers_;
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    public java.util.List<ProtocolModel.Block> getHeadersList() {
      return headers_;
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    public java.util.List<? extends ProtocolModel.BlockOrBuilder>
        getHeadersOrBuilderList() {
      return headers_;
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    public int getHeadersCount() {
      return headers_.size();
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    public ProtocolModel.Block getHeaders(int index) {
      return headers_.get(index);
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
     */
    public ProtocolModel.BlockOrBuilder getHeadersOrBuilder(
        int index) {
      return headers_.get(index);
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
      if (count_ != 0) {
        output.writeUInt32(1, count_);
      }
      for (int i = 0; i < headers_.size(); i++) {
        output.writeMessage(2, headers_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (count_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, count_);
      }
      for (int i = 0; i < headers_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, headers_.get(i));
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
      if (!(obj instanceof ProtocolModel.HeadersMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.HeadersMessage other = (ProtocolModel.HeadersMessage) obj;

      if (getCount()
          != other.getCount()) return false;
      if (!getHeadersList()
          .equals(other.getHeadersList())) return false;
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
      hash = (37 * hash) + COUNT_FIELD_NUMBER;
      hash = (53 * hash) + getCount();
      if (getHeadersCount() > 0) {
        hash = (37 * hash) + HEADERS_FIELD_NUMBER;
        hash = (53 * hash) + getHeadersList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.HeadersMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.HeadersMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.HeadersMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.HeadersMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.HeadersMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.HeadersMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.HeadersMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.HeadersMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.HeadersMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.HeadersMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.HeadersMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.HeadersMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.HeadersMessage prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.HeadersMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.HeadersMessage)
        ProtocolModel.HeadersMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_HeadersMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_HeadersMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.HeadersMessage.class, ProtocolModel.HeadersMessage.Builder.class);
      }

      // Construct using ProtocolModel.HeadersMessage.newBuilder()
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
          getHeadersFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        count_ = 0;

        if (headersBuilder_ == null) {
          headers_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000002);
        } else {
          headersBuilder_.clear();
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_HeadersMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.HeadersMessage getDefaultInstanceForType() {
        return ProtocolModel.HeadersMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.HeadersMessage build() {
        ProtocolModel.HeadersMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.HeadersMessage buildPartial() {
        ProtocolModel.HeadersMessage result = new ProtocolModel.HeadersMessage(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.count_ = count_;
        if (headersBuilder_ == null) {
          if (((bitField0_ & 0x00000002) != 0)) {
            headers_ = java.util.Collections.unmodifiableList(headers_);
            bitField0_ = (bitField0_ & ~0x00000002);
          }
          result.headers_ = headers_;
        } else {
          result.headers_ = headersBuilder_.build();
        }
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
        if (other instanceof ProtocolModel.HeadersMessage) {
          return mergeFrom((ProtocolModel.HeadersMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.HeadersMessage other) {
        if (other == ProtocolModel.HeadersMessage.getDefaultInstance()) return this;
        if (other.getCount() != 0) {
          setCount(other.getCount());
        }
        if (headersBuilder_ == null) {
          if (!other.headers_.isEmpty()) {
            if (headers_.isEmpty()) {
              headers_ = other.headers_;
              bitField0_ = (bitField0_ & ~0x00000002);
            } else {
              ensureHeadersIsMutable();
              headers_.addAll(other.headers_);
            }
            onChanged();
          }
        } else {
          if (!other.headers_.isEmpty()) {
            if (headersBuilder_.isEmpty()) {
              headersBuilder_.dispose();
              headersBuilder_ = null;
              headers_ = other.headers_;
              bitField0_ = (bitField0_ & ~0x00000002);
              headersBuilder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getHeadersFieldBuilder() : null;
            } else {
              headersBuilder_.addAllMessages(other.headers_);
            }
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
        ProtocolModel.HeadersMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.HeadersMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int count_ ;
      /**
       * <code>uint32 count = 1;</code>
       */
      public int getCount() {
        return count_;
      }
      /**
       * <code>uint32 count = 1;</code>
       */
      public Builder setCount(int value) {

        count_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 count = 1;</code>
       */
      public Builder clearCount() {

        count_ = 0;
        onChanged();
        return this;
      }

      private java.util.List<ProtocolModel.Block> headers_ =
        java.util.Collections.emptyList();
      private void ensureHeadersIsMutable() {
        if (!((bitField0_ & 0x00000002) != 0)) {
          headers_ = new java.util.ArrayList<ProtocolModel.Block>(headers_);
          bitField0_ |= 0x00000002;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          ProtocolModel.Block, ProtocolModel.Block.Builder, ProtocolModel.BlockOrBuilder> headersBuilder_;

      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public java.util.List<ProtocolModel.Block> getHeadersList() {
        if (headersBuilder_ == null) {
          return java.util.Collections.unmodifiableList(headers_);
        } else {
          return headersBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public int getHeadersCount() {
        if (headersBuilder_ == null) {
          return headers_.size();
        } else {
          return headersBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public ProtocolModel.Block getHeaders(int index) {
        if (headersBuilder_ == null) {
          return headers_.get(index);
        } else {
          return headersBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public Builder setHeaders(
          int index, ProtocolModel.Block value) {
        if (headersBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureHeadersIsMutable();
          headers_.set(index, value);
          onChanged();
        } else {
          headersBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public Builder setHeaders(
          int index, ProtocolModel.Block.Builder builderForValue) {
        if (headersBuilder_ == null) {
          ensureHeadersIsMutable();
          headers_.set(index, builderForValue.build());
          onChanged();
        } else {
          headersBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public Builder addHeaders(ProtocolModel.Block value) {
        if (headersBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureHeadersIsMutable();
          headers_.add(value);
          onChanged();
        } else {
          headersBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public Builder addHeaders(
          int index, ProtocolModel.Block value) {
        if (headersBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureHeadersIsMutable();
          headers_.add(index, value);
          onChanged();
        } else {
          headersBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public Builder addHeaders(
          ProtocolModel.Block.Builder builderForValue) {
        if (headersBuilder_ == null) {
          ensureHeadersIsMutable();
          headers_.add(builderForValue.build());
          onChanged();
        } else {
          headersBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public Builder addHeaders(
          int index, ProtocolModel.Block.Builder builderForValue) {
        if (headersBuilder_ == null) {
          ensureHeadersIsMutable();
          headers_.add(index, builderForValue.build());
          onChanged();
        } else {
          headersBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public Builder addAllHeaders(
          java.lang.Iterable<? extends ProtocolModel.Block> values) {
        if (headersBuilder_ == null) {
          ensureHeadersIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, headers_);
          onChanged();
        } else {
          headersBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public Builder clearHeaders() {
        if (headersBuilder_ == null) {
          headers_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000002);
          onChanged();
        } else {
          headersBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public Builder removeHeaders(int index) {
        if (headersBuilder_ == null) {
          ensureHeadersIsMutable();
          headers_.remove(index);
          onChanged();
        } else {
          headersBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public ProtocolModel.Block.Builder getHeadersBuilder(
          int index) {
        return getHeadersFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public ProtocolModel.BlockOrBuilder getHeadersOrBuilder(
          int index) {
        if (headersBuilder_ == null) {
          return headers_.get(index);  } else {
          return headersBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public java.util.List<? extends ProtocolModel.BlockOrBuilder>
           getHeadersOrBuilderList() {
        if (headersBuilder_ != null) {
          return headersBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(headers_);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public ProtocolModel.Block.Builder addHeadersBuilder() {
        return getHeadersFieldBuilder().addBuilder(
            ProtocolModel.Block.getDefaultInstance());
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public ProtocolModel.Block.Builder addHeadersBuilder(
          int index) {
        return getHeadersFieldBuilder().addBuilder(
            index, ProtocolModel.Block.getDefaultInstance());
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block headers = 2;</code>
       */
      public java.util.List<ProtocolModel.Block.Builder>
           getHeadersBuilderList() {
        return getHeadersFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          ProtocolModel.Block, ProtocolModel.Block.Builder, ProtocolModel.BlockOrBuilder>
          getHeadersFieldBuilder() {
        if (headersBuilder_ == null) {
          headersBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              ProtocolModel.Block, ProtocolModel.Block.Builder, ProtocolModel.BlockOrBuilder>(
                  headers_,
                  ((bitField0_ & 0x00000002) != 0),
                  getParentForChildren(),
                  isClean());
          headers_ = null;
        }
        return headersBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.HeadersMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.HeadersMessage)
    private static final ProtocolModel.HeadersMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.HeadersMessage();
    }

    public static ProtocolModel.HeadersMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<HeadersMessage>
        PARSER = new com.google.protobuf.AbstractParser<HeadersMessage>() {
      @java.lang.Override
      public HeadersMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new HeadersMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<HeadersMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<HeadersMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.HeadersMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface GetBlocksMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.GetBlocksMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 start_height = 1;</code>
     */
    int getStartHeight();

    /**
     * <code>uint32 stop_height = 2;</code>
     */
    int getStopHeight();

    /**
     * <code>bool clip_from_stop = 3;</code>
     */
    boolean getClipFromStop();
  }
  /**
   * <pre>
   * fetch canonical blocks
   * </pre>
   *
   * Protobuf type {@code org.ethereum.protobuf.tcp.GetBlocksMessage}
   */
  public  static final class GetBlocksMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.GetBlocksMessage)
      GetBlocksMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use GetBlocksMessage.newBuilder() to construct.
    private GetBlocksMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private GetBlocksMessage() {
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private GetBlocksMessage(
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

              startHeight_ = input.readUInt32();
              break;
            }
            case 16: {

              stopHeight_ = input.readUInt32();
              break;
            }
            case 24: {

              clipFromStop_ = input.readBool();
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.GetBlocksMessage.class, ProtocolModel.GetBlocksMessage.Builder.class);
    }

    public static final int START_HEIGHT_FIELD_NUMBER = 1;
    private int startHeight_;
    /**
     * <code>uint32 start_height = 1;</code>
     */
    public int getStartHeight() {
      return startHeight_;
    }

    public static final int STOP_HEIGHT_FIELD_NUMBER = 2;
    private int stopHeight_;
    /**
     * <code>uint32 stop_height = 2;</code>
     */
    public int getStopHeight() {
      return stopHeight_;
    }

    public static final int CLIP_FROM_STOP_FIELD_NUMBER = 3;
    private boolean clipFromStop_;
    /**
     * <code>bool clip_from_stop = 3;</code>
     */
    public boolean getClipFromStop() {
      return clipFromStop_;
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
      if (startHeight_ != 0) {
        output.writeUInt32(1, startHeight_);
      }
      if (stopHeight_ != 0) {
        output.writeUInt32(2, stopHeight_);
      }
      if (clipFromStop_ != false) {
        output.writeBool(3, clipFromStop_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (startHeight_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, startHeight_);
      }
      if (stopHeight_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(2, stopHeight_);
      }
      if (clipFromStop_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(3, clipFromStop_);
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
      if (!(obj instanceof ProtocolModel.GetBlocksMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.GetBlocksMessage other = (ProtocolModel.GetBlocksMessage) obj;

      if (getStartHeight()
          != other.getStartHeight()) return false;
      if (getStopHeight()
          != other.getStopHeight()) return false;
      if (getClipFromStop()
          != other.getClipFromStop()) return false;
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
      hash = (37 * hash) + START_HEIGHT_FIELD_NUMBER;
      hash = (53 * hash) + getStartHeight();
      hash = (37 * hash) + STOP_HEIGHT_FIELD_NUMBER;
      hash = (53 * hash) + getStopHeight();
      hash = (37 * hash) + CLIP_FROM_STOP_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getClipFromStop());
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.GetBlocksMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.GetBlocksMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.GetBlocksMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.GetBlocksMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.GetBlocksMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.GetBlocksMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.GetBlocksMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.GetBlocksMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.GetBlocksMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.GetBlocksMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.GetBlocksMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.GetBlocksMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.GetBlocksMessage prototype) {
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
     * <pre>
     * fetch canonical blocks
     * </pre>
     *
     * Protobuf type {@code org.ethereum.protobuf.tcp.GetBlocksMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.GetBlocksMessage)
        ProtocolModel.GetBlocksMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.GetBlocksMessage.class, ProtocolModel.GetBlocksMessage.Builder.class);
      }

      // Construct using ProtocolModel.GetBlocksMessage.newBuilder()
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
        startHeight_ = 0;

        stopHeight_ = 0;

        clipFromStop_ = false;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.GetBlocksMessage getDefaultInstanceForType() {
        return ProtocolModel.GetBlocksMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.GetBlocksMessage build() {
        ProtocolModel.GetBlocksMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.GetBlocksMessage buildPartial() {
        ProtocolModel.GetBlocksMessage result = new ProtocolModel.GetBlocksMessage(this);
        result.startHeight_ = startHeight_;
        result.stopHeight_ = stopHeight_;
        result.clipFromStop_ = clipFromStop_;
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
        if (other instanceof ProtocolModel.GetBlocksMessage) {
          return mergeFrom((ProtocolModel.GetBlocksMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.GetBlocksMessage other) {
        if (other == ProtocolModel.GetBlocksMessage.getDefaultInstance()) return this;
        if (other.getStartHeight() != 0) {
          setStartHeight(other.getStartHeight());
        }
        if (other.getStopHeight() != 0) {
          setStopHeight(other.getStopHeight());
        }
        if (other.getClipFromStop() != false) {
          setClipFromStop(other.getClipFromStop());
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
        ProtocolModel.GetBlocksMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.GetBlocksMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int startHeight_ ;
      /**
       * <code>uint32 start_height = 1;</code>
       */
      public int getStartHeight() {
        return startHeight_;
      }
      /**
       * <code>uint32 start_height = 1;</code>
       */
      public Builder setStartHeight(int value) {

        startHeight_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 start_height = 1;</code>
       */
      public Builder clearStartHeight() {

        startHeight_ = 0;
        onChanged();
        return this;
      }

      private int stopHeight_ ;
      /**
       * <code>uint32 stop_height = 2;</code>
       */
      public int getStopHeight() {
        return stopHeight_;
      }
      /**
       * <code>uint32 stop_height = 2;</code>
       */
      public Builder setStopHeight(int value) {

        stopHeight_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 stop_height = 2;</code>
       */
      public Builder clearStopHeight() {

        stopHeight_ = 0;
        onChanged();
        return this;
      }

      private boolean clipFromStop_ ;
      /**
       * <code>bool clip_from_stop = 3;</code>
       */
      public boolean getClipFromStop() {
        return clipFromStop_;
      }
      /**
       * <code>bool clip_from_stop = 3;</code>
       */
      public Builder setClipFromStop(boolean value) {

        clipFromStop_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bool clip_from_stop = 3;</code>
       */
      public Builder clearClipFromStop() {

        clipFromStop_ = false;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.GetBlocksMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.GetBlocksMessage)
    private static final ProtocolModel.GetBlocksMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.GetBlocksMessage();
    }

    public static ProtocolModel.GetBlocksMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<GetBlocksMessage>
        PARSER = new com.google.protobuf.AbstractParser<GetBlocksMessage>() {
      @java.lang.Override
      public GetBlocksMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new GetBlocksMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<GetBlocksMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<GetBlocksMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.GetBlocksMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface BlocksMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.BlocksMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 count = 1;</code>
     */
    int getCount();

    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    java.util.List<ProtocolModel.Block>
        getBlocksList();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    ProtocolModel.Block getBlocks(int index);
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    int getBlocksCount();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    java.util.List<? extends ProtocolModel.BlockOrBuilder>
        getBlocksOrBuilderList();
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    ProtocolModel.BlockOrBuilder getBlocksOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.BlocksMessage}
   */
  public  static final class BlocksMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.BlocksMessage)
      BlocksMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use BlocksMessage.newBuilder() to construct.
    private BlocksMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private BlocksMessage() {
      blocks_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private BlocksMessage(
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

              count_ = input.readUInt32();
              break;
            }
            case 18: {
              if (!((mutable_bitField0_ & 0x00000002) != 0)) {
                blocks_ = new java.util.ArrayList<ProtocolModel.Block>();
                mutable_bitField0_ |= 0x00000002;
              }
              blocks_.add(
                  input.readMessage(ProtocolModel.Block.parser(), extensionRegistry));
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
        if (((mutable_bitField0_ & 0x00000002) != 0)) {
          blocks_ = java.util.Collections.unmodifiableList(blocks_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_BlocksMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_BlocksMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.BlocksMessage.class, ProtocolModel.BlocksMessage.Builder.class);
    }

    private int bitField0_;
    public static final int COUNT_FIELD_NUMBER = 1;
    private int count_;
    /**
     * <code>uint32 count = 1;</code>
     */
    public int getCount() {
      return count_;
    }

    public static final int BLOCKS_FIELD_NUMBER = 2;
    private java.util.List<ProtocolModel.Block> blocks_;
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    public java.util.List<ProtocolModel.Block> getBlocksList() {
      return blocks_;
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    public java.util.List<? extends ProtocolModel.BlockOrBuilder>
        getBlocksOrBuilderList() {
      return blocks_;
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    public int getBlocksCount() {
      return blocks_.size();
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    public ProtocolModel.Block getBlocks(int index) {
      return blocks_.get(index);
    }
    /**
     * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
     */
    public ProtocolModel.BlockOrBuilder getBlocksOrBuilder(
        int index) {
      return blocks_.get(index);
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
      if (count_ != 0) {
        output.writeUInt32(1, count_);
      }
      for (int i = 0; i < blocks_.size(); i++) {
        output.writeMessage(2, blocks_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (count_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, count_);
      }
      for (int i = 0; i < blocks_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, blocks_.get(i));
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
      if (!(obj instanceof ProtocolModel.BlocksMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.BlocksMessage other = (ProtocolModel.BlocksMessage) obj;

      if (getCount()
          != other.getCount()) return false;
      if (!getBlocksList()
          .equals(other.getBlocksList())) return false;
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
      hash = (37 * hash) + COUNT_FIELD_NUMBER;
      hash = (53 * hash) + getCount();
      if (getBlocksCount() > 0) {
        hash = (37 * hash) + BLOCKS_FIELD_NUMBER;
        hash = (53 * hash) + getBlocksList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.BlocksMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.BlocksMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.BlocksMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.BlocksMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.BlocksMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.BlocksMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.BlocksMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.BlocksMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.BlocksMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.BlocksMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.BlocksMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.BlocksMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.BlocksMessage prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.BlocksMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.BlocksMessage)
        ProtocolModel.BlocksMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_BlocksMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_BlocksMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.BlocksMessage.class, ProtocolModel.BlocksMessage.Builder.class);
      }

      // Construct using ProtocolModel.BlocksMessage.newBuilder()
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
          getBlocksFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        count_ = 0;

        if (blocksBuilder_ == null) {
          blocks_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000002);
        } else {
          blocksBuilder_.clear();
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_BlocksMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.BlocksMessage getDefaultInstanceForType() {
        return ProtocolModel.BlocksMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.BlocksMessage build() {
        ProtocolModel.BlocksMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.BlocksMessage buildPartial() {
        ProtocolModel.BlocksMessage result = new ProtocolModel.BlocksMessage(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.count_ = count_;
        if (blocksBuilder_ == null) {
          if (((bitField0_ & 0x00000002) != 0)) {
            blocks_ = java.util.Collections.unmodifiableList(blocks_);
            bitField0_ = (bitField0_ & ~0x00000002);
          }
          result.blocks_ = blocks_;
        } else {
          result.blocks_ = blocksBuilder_.build();
        }
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
        if (other instanceof ProtocolModel.BlocksMessage) {
          return mergeFrom((ProtocolModel.BlocksMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.BlocksMessage other) {
        if (other == ProtocolModel.BlocksMessage.getDefaultInstance()) return this;
        if (other.getCount() != 0) {
          setCount(other.getCount());
        }
        if (blocksBuilder_ == null) {
          if (!other.blocks_.isEmpty()) {
            if (blocks_.isEmpty()) {
              blocks_ = other.blocks_;
              bitField0_ = (bitField0_ & ~0x00000002);
            } else {
              ensureBlocksIsMutable();
              blocks_.addAll(other.blocks_);
            }
            onChanged();
          }
        } else {
          if (!other.blocks_.isEmpty()) {
            if (blocksBuilder_.isEmpty()) {
              blocksBuilder_.dispose();
              blocksBuilder_ = null;
              blocks_ = other.blocks_;
              bitField0_ = (bitField0_ & ~0x00000002);
              blocksBuilder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getBlocksFieldBuilder() : null;
            } else {
              blocksBuilder_.addAllMessages(other.blocks_);
            }
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
        ProtocolModel.BlocksMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.BlocksMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int count_ ;
      /**
       * <code>uint32 count = 1;</code>
       */
      public int getCount() {
        return count_;
      }
      /**
       * <code>uint32 count = 1;</code>
       */
      public Builder setCount(int value) {

        count_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 count = 1;</code>
       */
      public Builder clearCount() {

        count_ = 0;
        onChanged();
        return this;
      }

      private java.util.List<ProtocolModel.Block> blocks_ =
        java.util.Collections.emptyList();
      private void ensureBlocksIsMutable() {
        if (!((bitField0_ & 0x00000002) != 0)) {
          blocks_ = new java.util.ArrayList<ProtocolModel.Block>(blocks_);
          bitField0_ |= 0x00000002;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          ProtocolModel.Block, ProtocolModel.Block.Builder, ProtocolModel.BlockOrBuilder> blocksBuilder_;

      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public java.util.List<ProtocolModel.Block> getBlocksList() {
        if (blocksBuilder_ == null) {
          return java.util.Collections.unmodifiableList(blocks_);
        } else {
          return blocksBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public int getBlocksCount() {
        if (blocksBuilder_ == null) {
          return blocks_.size();
        } else {
          return blocksBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public ProtocolModel.Block getBlocks(int index) {
        if (blocksBuilder_ == null) {
          return blocks_.get(index);
        } else {
          return blocksBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public Builder setBlocks(
          int index, ProtocolModel.Block value) {
        if (blocksBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureBlocksIsMutable();
          blocks_.set(index, value);
          onChanged();
        } else {
          blocksBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public Builder setBlocks(
          int index, ProtocolModel.Block.Builder builderForValue) {
        if (blocksBuilder_ == null) {
          ensureBlocksIsMutable();
          blocks_.set(index, builderForValue.build());
          onChanged();
        } else {
          blocksBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public Builder addBlocks(ProtocolModel.Block value) {
        if (blocksBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureBlocksIsMutable();
          blocks_.add(value);
          onChanged();
        } else {
          blocksBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public Builder addBlocks(
          int index, ProtocolModel.Block value) {
        if (blocksBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureBlocksIsMutable();
          blocks_.add(index, value);
          onChanged();
        } else {
          blocksBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public Builder addBlocks(
          ProtocolModel.Block.Builder builderForValue) {
        if (blocksBuilder_ == null) {
          ensureBlocksIsMutable();
          blocks_.add(builderForValue.build());
          onChanged();
        } else {
          blocksBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public Builder addBlocks(
          int index, ProtocolModel.Block.Builder builderForValue) {
        if (blocksBuilder_ == null) {
          ensureBlocksIsMutable();
          blocks_.add(index, builderForValue.build());
          onChanged();
        } else {
          blocksBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public Builder addAllBlocks(
          java.lang.Iterable<? extends ProtocolModel.Block> values) {
        if (blocksBuilder_ == null) {
          ensureBlocksIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, blocks_);
          onChanged();
        } else {
          blocksBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public Builder clearBlocks() {
        if (blocksBuilder_ == null) {
          blocks_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000002);
          onChanged();
        } else {
          blocksBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public Builder removeBlocks(int index) {
        if (blocksBuilder_ == null) {
          ensureBlocksIsMutable();
          blocks_.remove(index);
          onChanged();
        } else {
          blocksBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public ProtocolModel.Block.Builder getBlocksBuilder(
          int index) {
        return getBlocksFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public ProtocolModel.BlockOrBuilder getBlocksOrBuilder(
          int index) {
        if (blocksBuilder_ == null) {
          return blocks_.get(index);  } else {
          return blocksBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public java.util.List<? extends ProtocolModel.BlockOrBuilder>
           getBlocksOrBuilderList() {
        if (blocksBuilder_ != null) {
          return blocksBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(blocks_);
        }
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public ProtocolModel.Block.Builder addBlocksBuilder() {
        return getBlocksFieldBuilder().addBuilder(
            ProtocolModel.Block.getDefaultInstance());
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public ProtocolModel.Block.Builder addBlocksBuilder(
          int index) {
        return getBlocksFieldBuilder().addBuilder(
            index, ProtocolModel.Block.getDefaultInstance());
      }
      /**
       * <code>repeated .org.ethereum.protobuf.tcp.Block blocks = 2;</code>
       */
      public java.util.List<ProtocolModel.Block.Builder>
           getBlocksBuilderList() {
        return getBlocksFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          ProtocolModel.Block, ProtocolModel.Block.Builder, ProtocolModel.BlockOrBuilder>
          getBlocksFieldBuilder() {
        if (blocksBuilder_ == null) {
          blocksBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              ProtocolModel.Block, ProtocolModel.Block.Builder, ProtocolModel.BlockOrBuilder>(
                  blocks_,
                  ((bitField0_ & 0x00000002) != 0),
                  getParentForChildren(),
                  isClean());
          blocks_ = null;
        }
        return blocksBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.BlocksMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.BlocksMessage)
    private static final ProtocolModel.BlocksMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.BlocksMessage();
    }

    public static ProtocolModel.BlocksMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<BlocksMessage>
        PARSER = new com.google.protobuf.AbstractParser<BlocksMessage>() {
      @java.lang.Override
      public BlocksMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new BlocksMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<BlocksMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<BlocksMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.BlocksMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface P2PMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.P2PMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>.org.ethereum.protobuf.tcp.P2PMessage.Type type = 1;</code>
     */
    int getTypeValue();
    /**
     * <code>.org.ethereum.protobuf.tcp.P2PMessage.Type type = 1;</code>
     */
    ProtocolModel.P2PMessage.Type getType();

    /**
     * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
     */
    boolean hasDisconnectMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
     */
    ProtocolModel.DisconnectMessage getDisconnectMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
     */
    ProtocolModel.DisconnectMessageOrBuilder getDisconnectMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
     */
    boolean hasPingMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
     */
    ProtocolModel.PingMessage getPingMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
     */
    ProtocolModel.PingMessageOrBuilder getPingMessageOrBuilder();

    /**
     * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
     */
    boolean hasPongMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
     */
    ProtocolModel.PongMessage getPongMessage();
    /**
     * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
     */
    ProtocolModel.PongMessageOrBuilder getPongMessageOrBuilder();

    public ProtocolModel.P2PMessage.DataMsgCase getDataMsgCase();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.P2PMessage}
   */
  public  static final class P2PMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.P2PMessage)
      P2PMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use P2PMessage.newBuilder() to construct.
    private P2PMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private P2PMessage() {
      type_ = 0;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private P2PMessage(
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
              ProtocolModel.DisconnectMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 2) {
                subBuilder = ((ProtocolModel.DisconnectMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.DisconnectMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.DisconnectMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 2;
              break;
            }
            case 26: {
              ProtocolModel.PingMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 3) {
                subBuilder = ((ProtocolModel.PingMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.PingMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.PingMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 3;
              break;
            }
            case 34: {
              ProtocolModel.PongMessage.Builder subBuilder = null;
              if (dataMsgCase_ == 4) {
                subBuilder = ((ProtocolModel.PongMessage) dataMsg_).toBuilder();
              }
              dataMsg_ =
                  input.readMessage(ProtocolModel.PongMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((ProtocolModel.PongMessage) dataMsg_);
                dataMsg_ = subBuilder.buildPartial();
              }
              dataMsgCase_ = 4;
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_P2PMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_P2PMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.P2PMessage.class, ProtocolModel.P2PMessage.Builder.class);
    }

    /**
     * Protobuf enum {@code org.ethereum.protobuf.tcp.P2PMessage.Type}
     */
    public enum Type
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>DISCONNECT = 0;</code>
       */
      DISCONNECT(0),
      /**
       * <code>PING = 1;</code>
       */
      PING(1),
      /**
       * <code>PONG = 2;</code>
       */
      PONG(2),
      UNRECOGNIZED(-1),
      ;

      /**
       * <code>DISCONNECT = 0;</code>
       */
      public static final int DISCONNECT_VALUE = 0;
      /**
       * <code>PING = 1;</code>
       */
      public static final int PING_VALUE = 1;
      /**
       * <code>PONG = 2;</code>
       */
      public static final int PONG_VALUE = 2;


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
      public static Type valueOf(int value) {
        return forNumber(value);
      }

      public static Type forNumber(int value) {
        switch (value) {
          case 0: return DISCONNECT;
          case 1: return PING;
          case 2: return PONG;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<Type>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          Type> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<Type>() {
              public Type findValueByNumber(int number) {
                return Type.forNumber(number);
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
        return ProtocolModel.P2PMessage.getDescriptor().getEnumTypes().get(0);
      }

      private static final Type[] VALUES = values();

      public static Type valueOf(
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

      private Type(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:org.ethereum.protobuf.tcp.P2PMessage.Type)
    }

    private int dataMsgCase_ = 0;
    private java.lang.Object dataMsg_;
    public enum DataMsgCase
        implements com.google.protobuf.Internal.EnumLite {
      DISCONNECT_MESSAGE(2),
      PING_MESSAGE(3),
      PONG_MESSAGE(4),
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
          case 2: return DISCONNECT_MESSAGE;
          case 3: return PING_MESSAGE;
          case 4: return PONG_MESSAGE;
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
     * <code>.org.ethereum.protobuf.tcp.P2PMessage.Type type = 1;</code>
     */
    public int getTypeValue() {
      return type_;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.P2PMessage.Type type = 1;</code>
     */
    public ProtocolModel.P2PMessage.Type getType() {
      @SuppressWarnings("deprecation")
      ProtocolModel.P2PMessage.Type result = ProtocolModel.P2PMessage.Type.valueOf(type_);
      return result == null ? ProtocolModel.P2PMessage.Type.UNRECOGNIZED : result;
    }

    public static final int DISCONNECT_MESSAGE_FIELD_NUMBER = 2;
    /**
     * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
     */
    public boolean hasDisconnectMessage() {
      return dataMsgCase_ == 2;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
     */
    public ProtocolModel.DisconnectMessage getDisconnectMessage() {
      if (dataMsgCase_ == 2) {
         return (ProtocolModel.DisconnectMessage) dataMsg_;
      }
      return ProtocolModel.DisconnectMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
     */
    public ProtocolModel.DisconnectMessageOrBuilder getDisconnectMessageOrBuilder() {
      if (dataMsgCase_ == 2) {
         return (ProtocolModel.DisconnectMessage) dataMsg_;
      }
      return ProtocolModel.DisconnectMessage.getDefaultInstance();
    }

    public static final int PING_MESSAGE_FIELD_NUMBER = 3;
    /**
     * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
     */
    public boolean hasPingMessage() {
      return dataMsgCase_ == 3;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
     */
    public ProtocolModel.PingMessage getPingMessage() {
      if (dataMsgCase_ == 3) {
         return (ProtocolModel.PingMessage) dataMsg_;
      }
      return ProtocolModel.PingMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
     */
    public ProtocolModel.PingMessageOrBuilder getPingMessageOrBuilder() {
      if (dataMsgCase_ == 3) {
         return (ProtocolModel.PingMessage) dataMsg_;
      }
      return ProtocolModel.PingMessage.getDefaultInstance();
    }

    public static final int PONG_MESSAGE_FIELD_NUMBER = 4;
    /**
     * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
     */
    public boolean hasPongMessage() {
      return dataMsgCase_ == 4;
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
     */
    public ProtocolModel.PongMessage getPongMessage() {
      if (dataMsgCase_ == 4) {
         return (ProtocolModel.PongMessage) dataMsg_;
      }
      return ProtocolModel.PongMessage.getDefaultInstance();
    }
    /**
     * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
     */
    public ProtocolModel.PongMessageOrBuilder getPongMessageOrBuilder() {
      if (dataMsgCase_ == 4) {
         return (ProtocolModel.PongMessage) dataMsg_;
      }
      return ProtocolModel.PongMessage.getDefaultInstance();
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
      if (type_ != ProtocolModel.P2PMessage.Type.DISCONNECT.getNumber()) {
        output.writeEnum(1, type_);
      }
      if (dataMsgCase_ == 2) {
        output.writeMessage(2, (ProtocolModel.DisconnectMessage) dataMsg_);
      }
      if (dataMsgCase_ == 3) {
        output.writeMessage(3, (ProtocolModel.PingMessage) dataMsg_);
      }
      if (dataMsgCase_ == 4) {
        output.writeMessage(4, (ProtocolModel.PongMessage) dataMsg_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (type_ != ProtocolModel.P2PMessage.Type.DISCONNECT.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, type_);
      }
      if (dataMsgCase_ == 2) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, (ProtocolModel.DisconnectMessage) dataMsg_);
      }
      if (dataMsgCase_ == 3) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, (ProtocolModel.PingMessage) dataMsg_);
      }
      if (dataMsgCase_ == 4) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, (ProtocolModel.PongMessage) dataMsg_);
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
      if (!(obj instanceof ProtocolModel.P2PMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.P2PMessage other = (ProtocolModel.P2PMessage) obj;

      if (type_ != other.type_) return false;
      if (!getDataMsgCase().equals(other.getDataMsgCase())) return false;
      switch (dataMsgCase_) {
        case 2:
          if (!getDisconnectMessage()
              .equals(other.getDisconnectMessage())) return false;
          break;
        case 3:
          if (!getPingMessage()
              .equals(other.getPingMessage())) return false;
          break;
        case 4:
          if (!getPongMessage()
              .equals(other.getPongMessage())) return false;
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
      switch (dataMsgCase_) {
        case 2:
          hash = (37 * hash) + DISCONNECT_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getDisconnectMessage().hashCode();
          break;
        case 3:
          hash = (37 * hash) + PING_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getPingMessage().hashCode();
          break;
        case 4:
          hash = (37 * hash) + PONG_MESSAGE_FIELD_NUMBER;
          hash = (53 * hash) + getPongMessage().hashCode();
          break;
        case 0:
        default:
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.P2PMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.P2PMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.P2PMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.P2PMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.P2PMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.P2PMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.P2PMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.P2PMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.P2PMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.P2PMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.P2PMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.P2PMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.P2PMessage prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.P2PMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.P2PMessage)
        ProtocolModel.P2PMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_P2PMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_P2PMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.P2PMessage.class, ProtocolModel.P2PMessage.Builder.class);
      }

      // Construct using ProtocolModel.P2PMessage.newBuilder()
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

        dataMsgCase_ = 0;
        dataMsg_ = null;
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_P2PMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.P2PMessage getDefaultInstanceForType() {
        return ProtocolModel.P2PMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.P2PMessage build() {
        ProtocolModel.P2PMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.P2PMessage buildPartial() {
        ProtocolModel.P2PMessage result = new ProtocolModel.P2PMessage(this);
        result.type_ = type_;
        if (dataMsgCase_ == 2) {
          if (disconnectMessageBuilder_ == null) {
            result.dataMsg_ = dataMsg_;
          } else {
            result.dataMsg_ = disconnectMessageBuilder_.build();
          }
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
        if (other instanceof ProtocolModel.P2PMessage) {
          return mergeFrom((ProtocolModel.P2PMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.P2PMessage other) {
        if (other == ProtocolModel.P2PMessage.getDefaultInstance()) return this;
        if (other.type_ != 0) {
          setTypeValue(other.getTypeValue());
        }
        switch (other.getDataMsgCase()) {
          case DISCONNECT_MESSAGE: {
            mergeDisconnectMessage(other.getDisconnectMessage());
            break;
          }
          case PING_MESSAGE: {
            mergePingMessage(other.getPingMessage());
            break;
          }
          case PONG_MESSAGE: {
            mergePongMessage(other.getPongMessage());
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
        ProtocolModel.P2PMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.P2PMessage) e.getUnfinishedMessage();
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
       * <code>.org.ethereum.protobuf.tcp.P2PMessage.Type type = 1;</code>
       */
      public int getTypeValue() {
        return type_;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage.Type type = 1;</code>
       */
      public Builder setTypeValue(int value) {
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage.Type type = 1;</code>
       */
      public ProtocolModel.P2PMessage.Type getType() {
        @SuppressWarnings("deprecation")
        ProtocolModel.P2PMessage.Type result = ProtocolModel.P2PMessage.Type.valueOf(type_);
        return result == null ? ProtocolModel.P2PMessage.Type.UNRECOGNIZED : result;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage.Type type = 1;</code>
       */
      public Builder setType(ProtocolModel.P2PMessage.Type value) {
        if (value == null) {
          throw new NullPointerException();
        }

        type_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.P2PMessage.Type type = 1;</code>
       */
      public Builder clearType() {

        type_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.DisconnectMessage, ProtocolModel.DisconnectMessage.Builder, ProtocolModel.DisconnectMessageOrBuilder> disconnectMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
       */
      public boolean hasDisconnectMessage() {
        return dataMsgCase_ == 2;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
       */
      public ProtocolModel.DisconnectMessage getDisconnectMessage() {
        if (disconnectMessageBuilder_ == null) {
          if (dataMsgCase_ == 2) {
            return (ProtocolModel.DisconnectMessage) dataMsg_;
          }
          return ProtocolModel.DisconnectMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 2) {
            return disconnectMessageBuilder_.getMessage();
          }
          return ProtocolModel.DisconnectMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
       */
      public Builder setDisconnectMessage(ProtocolModel.DisconnectMessage value) {
        if (disconnectMessageBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          dataMsg_ = value;
          onChanged();
        } else {
          disconnectMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 2;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
       */
      public Builder setDisconnectMessage(
          ProtocolModel.DisconnectMessage.Builder builderForValue) {
        if (disconnectMessageBuilder_ == null) {
          dataMsg_ = builderForValue.build();
          onChanged();
        } else {
          disconnectMessageBuilder_.setMessage(builderForValue.build());
        }
        dataMsgCase_ = 2;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
       */
      public Builder mergeDisconnectMessage(ProtocolModel.DisconnectMessage value) {
        if (disconnectMessageBuilder_ == null) {
          if (dataMsgCase_ == 2 &&
              dataMsg_ != ProtocolModel.DisconnectMessage.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.DisconnectMessage.newBuilder((ProtocolModel.DisconnectMessage) dataMsg_)
                .mergeFrom(value).buildPartial();
          } else {
            dataMsg_ = value;
          }
          onChanged();
        } else {
          if (dataMsgCase_ == 2) {
            disconnectMessageBuilder_.mergeFrom(value);
          }
          disconnectMessageBuilder_.setMessage(value);
        }
        dataMsgCase_ = 2;
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
       */
      public Builder clearDisconnectMessage() {
        if (disconnectMessageBuilder_ == null) {
          if (dataMsgCase_ == 2) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
            onChanged();
          }
        } else {
          if (dataMsgCase_ == 2) {
            dataMsgCase_ = 0;
            dataMsg_ = null;
          }
          disconnectMessageBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
       */
      public ProtocolModel.DisconnectMessage.Builder getDisconnectMessageBuilder() {
        return getDisconnectMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
       */
      public ProtocolModel.DisconnectMessageOrBuilder getDisconnectMessageOrBuilder() {
        if ((dataMsgCase_ == 2) && (disconnectMessageBuilder_ != null)) {
          return disconnectMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 2) {
            return (ProtocolModel.DisconnectMessage) dataMsg_;
          }
          return ProtocolModel.DisconnectMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.DisconnectMessage disconnect_message = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.DisconnectMessage, ProtocolModel.DisconnectMessage.Builder, ProtocolModel.DisconnectMessageOrBuilder>
          getDisconnectMessageFieldBuilder() {
        if (disconnectMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 2)) {
            dataMsg_ = ProtocolModel.DisconnectMessage.getDefaultInstance();
          }
          disconnectMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.DisconnectMessage, ProtocolModel.DisconnectMessage.Builder, ProtocolModel.DisconnectMessageOrBuilder>(
                  (ProtocolModel.DisconnectMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 2;
        onChanged();;
        return disconnectMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.PingMessage, ProtocolModel.PingMessage.Builder, ProtocolModel.PingMessageOrBuilder> pingMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
       */
      public boolean hasPingMessage() {
        return dataMsgCase_ == 3;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
       */
      public ProtocolModel.PingMessage getPingMessage() {
        if (pingMessageBuilder_ == null) {
          if (dataMsgCase_ == 3) {
            return (ProtocolModel.PingMessage) dataMsg_;
          }
          return ProtocolModel.PingMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 3) {
            return pingMessageBuilder_.getMessage();
          }
          return ProtocolModel.PingMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
       */
      public Builder setPingMessage(ProtocolModel.PingMessage value) {
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
       * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
       */
      public Builder setPingMessage(
          ProtocolModel.PingMessage.Builder builderForValue) {
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
       * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
       */
      public Builder mergePingMessage(ProtocolModel.PingMessage value) {
        if (pingMessageBuilder_ == null) {
          if (dataMsgCase_ == 3 &&
              dataMsg_ != ProtocolModel.PingMessage.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.PingMessage.newBuilder((ProtocolModel.PingMessage) dataMsg_)
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
       * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
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
       * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
       */
      public ProtocolModel.PingMessage.Builder getPingMessageBuilder() {
        return getPingMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
       */
      public ProtocolModel.PingMessageOrBuilder getPingMessageOrBuilder() {
        if ((dataMsgCase_ == 3) && (pingMessageBuilder_ != null)) {
          return pingMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 3) {
            return (ProtocolModel.PingMessage) dataMsg_;
          }
          return ProtocolModel.PingMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.PingMessage ping_message = 3;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.PingMessage, ProtocolModel.PingMessage.Builder, ProtocolModel.PingMessageOrBuilder>
          getPingMessageFieldBuilder() {
        if (pingMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 3)) {
            dataMsg_ = ProtocolModel.PingMessage.getDefaultInstance();
          }
          pingMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.PingMessage, ProtocolModel.PingMessage.Builder, ProtocolModel.PingMessageOrBuilder>(
                  (ProtocolModel.PingMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 3;
        onChanged();;
        return pingMessageBuilder_;
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.PongMessage, ProtocolModel.PongMessage.Builder, ProtocolModel.PongMessageOrBuilder> pongMessageBuilder_;
      /**
       * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
       */
      public boolean hasPongMessage() {
        return dataMsgCase_ == 4;
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
       */
      public ProtocolModel.PongMessage getPongMessage() {
        if (pongMessageBuilder_ == null) {
          if (dataMsgCase_ == 4) {
            return (ProtocolModel.PongMessage) dataMsg_;
          }
          return ProtocolModel.PongMessage.getDefaultInstance();
        } else {
          if (dataMsgCase_ == 4) {
            return pongMessageBuilder_.getMessage();
          }
          return ProtocolModel.PongMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
       */
      public Builder setPongMessage(ProtocolModel.PongMessage value) {
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
       * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
       */
      public Builder setPongMessage(
          ProtocolModel.PongMessage.Builder builderForValue) {
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
       * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
       */
      public Builder mergePongMessage(ProtocolModel.PongMessage value) {
        if (pongMessageBuilder_ == null) {
          if (dataMsgCase_ == 4 &&
              dataMsg_ != ProtocolModel.PongMessage.getDefaultInstance()) {
            dataMsg_ = ProtocolModel.PongMessage.newBuilder((ProtocolModel.PongMessage) dataMsg_)
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
       * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
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
       * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
       */
      public ProtocolModel.PongMessage.Builder getPongMessageBuilder() {
        return getPongMessageFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
       */
      public ProtocolModel.PongMessageOrBuilder getPongMessageOrBuilder() {
        if ((dataMsgCase_ == 4) && (pongMessageBuilder_ != null)) {
          return pongMessageBuilder_.getMessageOrBuilder();
        } else {
          if (dataMsgCase_ == 4) {
            return (ProtocolModel.PongMessage) dataMsg_;
          }
          return ProtocolModel.PongMessage.getDefaultInstance();
        }
      }
      /**
       * <code>.org.ethereum.protobuf.tcp.PongMessage pong_message = 4;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          ProtocolModel.PongMessage, ProtocolModel.PongMessage.Builder, ProtocolModel.PongMessageOrBuilder>
          getPongMessageFieldBuilder() {
        if (pongMessageBuilder_ == null) {
          if (!(dataMsgCase_ == 4)) {
            dataMsg_ = ProtocolModel.PongMessage.getDefaultInstance();
          }
          pongMessageBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              ProtocolModel.PongMessage, ProtocolModel.PongMessage.Builder, ProtocolModel.PongMessageOrBuilder>(
                  (ProtocolModel.PongMessage) dataMsg_,
                  getParentForChildren(),
                  isClean());
          dataMsg_ = null;
        }
        dataMsgCase_ = 4;
        onChanged();;
        return pongMessageBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.P2PMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.P2PMessage)
    private static final ProtocolModel.P2PMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.P2PMessage();
    }

    public static ProtocolModel.P2PMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<P2PMessage>
        PARSER = new com.google.protobuf.AbstractParser<P2PMessage>() {
      @java.lang.Override
      public P2PMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new P2PMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<P2PMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<P2PMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.P2PMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface DisconnectMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.DisconnectMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string reason_code = 1;</code>
     */
    java.lang.String getReasonCode();
    /**
     * <code>string reason_code = 1;</code>
     */
    com.google.protobuf.ByteString
        getReasonCodeBytes();
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.DisconnectMessage}
   */
  public  static final class DisconnectMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.DisconnectMessage)
      DisconnectMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use DisconnectMessage.newBuilder() to construct.
    private DisconnectMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private DisconnectMessage() {
      reasonCode_ = "";
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private DisconnectMessage(
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

              reasonCode_ = s;
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.DisconnectMessage.class, ProtocolModel.DisconnectMessage.Builder.class);
    }

    public static final int REASON_CODE_FIELD_NUMBER = 1;
    private volatile java.lang.Object reasonCode_;
    /**
     * <code>string reason_code = 1;</code>
     */
    public java.lang.String getReasonCode() {
      java.lang.Object ref = reasonCode_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        reasonCode_ = s;
        return s;
      }
    }
    /**
     * <code>string reason_code = 1;</code>
     */
    public com.google.protobuf.ByteString
        getReasonCodeBytes() {
      java.lang.Object ref = reasonCode_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        reasonCode_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
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
      if (!getReasonCodeBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, reasonCode_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getReasonCodeBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, reasonCode_);
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
      if (!(obj instanceof ProtocolModel.DisconnectMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.DisconnectMessage other = (ProtocolModel.DisconnectMessage) obj;

      if (!getReasonCode()
          .equals(other.getReasonCode())) return false;
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
      hash = (37 * hash) + REASON_CODE_FIELD_NUMBER;
      hash = (53 * hash) + getReasonCode().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.DisconnectMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.DisconnectMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.DisconnectMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.DisconnectMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.DisconnectMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.DisconnectMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.DisconnectMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.DisconnectMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.DisconnectMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.DisconnectMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.DisconnectMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.DisconnectMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.DisconnectMessage prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.DisconnectMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.DisconnectMessage)
        ProtocolModel.DisconnectMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.DisconnectMessage.class, ProtocolModel.DisconnectMessage.Builder.class);
      }

      // Construct using ProtocolModel.DisconnectMessage.newBuilder()
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
        reasonCode_ = "";

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.DisconnectMessage getDefaultInstanceForType() {
        return ProtocolModel.DisconnectMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.DisconnectMessage build() {
        ProtocolModel.DisconnectMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.DisconnectMessage buildPartial() {
        ProtocolModel.DisconnectMessage result = new ProtocolModel.DisconnectMessage(this);
        result.reasonCode_ = reasonCode_;
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
        if (other instanceof ProtocolModel.DisconnectMessage) {
          return mergeFrom((ProtocolModel.DisconnectMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.DisconnectMessage other) {
        if (other == ProtocolModel.DisconnectMessage.getDefaultInstance()) return this;
        if (!other.getReasonCode().isEmpty()) {
          reasonCode_ = other.reasonCode_;
          onChanged();
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
        ProtocolModel.DisconnectMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.DisconnectMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object reasonCode_ = "";
      /**
       * <code>string reason_code = 1;</code>
       */
      public java.lang.String getReasonCode() {
        java.lang.Object ref = reasonCode_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          reasonCode_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string reason_code = 1;</code>
       */
      public com.google.protobuf.ByteString
          getReasonCodeBytes() {
        java.lang.Object ref = reasonCode_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          reasonCode_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string reason_code = 1;</code>
       */
      public Builder setReasonCode(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }

        reasonCode_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string reason_code = 1;</code>
       */
      public Builder clearReasonCode() {

        reasonCode_ = getDefaultInstance().getReasonCode();
        onChanged();
        return this;
      }
      /**
       * <code>string reason_code = 1;</code>
       */
      public Builder setReasonCodeBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

        reasonCode_ = value;
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.DisconnectMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.DisconnectMessage)
    private static final ProtocolModel.DisconnectMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.DisconnectMessage();
    }

    public static ProtocolModel.DisconnectMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<DisconnectMessage>
        PARSER = new com.google.protobuf.AbstractParser<DisconnectMessage>() {
      @java.lang.Override
      public DisconnectMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new DisconnectMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<DisconnectMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<DisconnectMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public ProtocolModel.DisconnectMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface PingMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.PingMessage)
      com.google.protobuf.MessageOrBuilder {
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.PingMessage}
   */
  public  static final class PingMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.PingMessage)
      PingMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use PingMessage.newBuilder() to construct.
    private PingMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private PingMessage() {
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PingMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PingMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.PingMessage.class, ProtocolModel.PingMessage.Builder.class);
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
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof ProtocolModel.PingMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.PingMessage other = (ProtocolModel.PingMessage) obj;

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
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.PingMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.PingMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.PingMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.PingMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.PingMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.PingMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.PingMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.PingMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.PingMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.PingMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.PingMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.PingMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.PingMessage prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.PingMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.PingMessage)
        ProtocolModel.PingMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PingMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PingMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.PingMessage.class, ProtocolModel.PingMessage.Builder.class);
      }

      // Construct using ProtocolModel.PingMessage.newBuilder()
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
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PingMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.PingMessage getDefaultInstanceForType() {
        return ProtocolModel.PingMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.PingMessage build() {
        ProtocolModel.PingMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.PingMessage buildPartial() {
        ProtocolModel.PingMessage result = new ProtocolModel.PingMessage(this);
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
        if (other instanceof ProtocolModel.PingMessage) {
          return mergeFrom((ProtocolModel.PingMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.PingMessage other) {
        if (other == ProtocolModel.PingMessage.getDefaultInstance()) return this;
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
        ProtocolModel.PingMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.PingMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.PingMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.PingMessage)
    private static final ProtocolModel.PingMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.PingMessage();
    }

    public static ProtocolModel.PingMessage getDefaultInstance() {
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
    public ProtocolModel.PingMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface PongMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.ethereum.protobuf.tcp.PongMessage)
      com.google.protobuf.MessageOrBuilder {
  }
  /**
   * Protobuf type {@code org.ethereum.protobuf.tcp.PongMessage}
   */
  public  static final class PongMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.ethereum.protobuf.tcp.PongMessage)
      PongMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use PongMessage.newBuilder() to construct.
    private PongMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private PongMessage() {
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
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PongMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PongMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtocolModel.PongMessage.class, ProtocolModel.PongMessage.Builder.class);
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
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof ProtocolModel.PongMessage)) {
        return super.equals(obj);
      }
      ProtocolModel.PongMessage other = (ProtocolModel.PongMessage) obj;

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
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProtocolModel.PongMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.PongMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.PongMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.PongMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.PongMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtocolModel.PongMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtocolModel.PongMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.PongMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.PongMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProtocolModel.PongMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProtocolModel.PongMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProtocolModel.PongMessage parseFrom(
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
    public static Builder newBuilder(ProtocolModel.PongMessage prototype) {
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
     * Protobuf type {@code org.ethereum.protobuf.tcp.PongMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.ethereum.protobuf.tcp.PongMessage)
        ProtocolModel.PongMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PongMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PongMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtocolModel.PongMessage.class, ProtocolModel.PongMessage.Builder.class);
      }

      // Construct using ProtocolModel.PongMessage.newBuilder()
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
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProtocolModel.internal_static_org_ethereum_protobuf_tcp_PongMessage_descriptor;
      }

      @java.lang.Override
      public ProtocolModel.PongMessage getDefaultInstanceForType() {
        return ProtocolModel.PongMessage.getDefaultInstance();
      }

      @java.lang.Override
      public ProtocolModel.PongMessage build() {
        ProtocolModel.PongMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public ProtocolModel.PongMessage buildPartial() {
        ProtocolModel.PongMessage result = new ProtocolModel.PongMessage(this);
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
        if (other instanceof ProtocolModel.PongMessage) {
          return mergeFrom((ProtocolModel.PongMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtocolModel.PongMessage other) {
        if (other == ProtocolModel.PongMessage.getDefaultInstance()) return this;
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
        ProtocolModel.PongMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtocolModel.PongMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
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


      // @@protoc_insertion_point(builder_scope:org.ethereum.protobuf.tcp.PongMessage)
    }

    // @@protoc_insertion_point(class_scope:org.ethereum.protobuf.tcp.PongMessage)
    private static final ProtocolModel.PongMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProtocolModel.PongMessage();
    }

    public static ProtocolModel.PongMessage getDefaultInstance() {
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
    public ProtocolModel.PongMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_Message_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_Message_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_Block_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_Block_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_Transaction_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_Transaction_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_StatusMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_StatusMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_HeadersMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_HeadersMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_BlocksMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_BlocksMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_P2PMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_P2PMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_PingMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_PingMessage_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_ethereum_protobuf_tcp_PongMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_ethereum_protobuf_tcp_PongMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016Protocol.proto\022\031org.ethereum.protobuf." +
      "tcp\"\260\002\n\007Message\0225\n\004type\030\001 \001(\0162\'.org.ethe" +
      "reum.protobuf.tcp.Message.Type\022\020\n\010answer" +
      "ed\030\002 \001(\010\022\026\n\016last_timestamp\030\003 \001(\004\022\023\n\013retr" +
      "y_times\030\004 \001(\004\022<\n\013p2p_message\030\005 \001(\0132%.org" +
      ".ethereum.protobuf.tcp.P2PMessageH\000\022F\n\020p" +
      "rotocol_message\030\006 \001(\0132*.org.ethereum.pro" +
      "tobuf.tcp.ProtocolMessageH\000\"\035\n\004Type\022\007\n\003P" +
      "2P\020\000\022\014\n\010PROTOCOL\020\001B\n\n\010data_msg\"R\n\023Transa" +
      "ctionsMessage\022;\n\013transaction\030\001 \003(\0132&.org" +
      ".ethereum.protobuf.tcp.Transaction\"\220\004\n\017P" +
      "rotocolMessage\022=\n\004type\030\001 \001(\0162/.org.ether" +
      "eum.protobuf.tcp.ProtocolMessage.Type\022B\n" +
      "\016status_message\030\002 \001(\0132(.org.ethereum.pro" +
      "tobuf.tcp.StatusMessageH\000\022I\n\022get_blocks_" +
      "message\030\003 \001(\0132+.org.ethereum.protobuf.tc" +
      "p.GetBlocksMessageH\000\022B\n\016blocks_message\030\004" +
      " \001(\0132(.org.ethereum.protobuf.tcp.BlocksM" +
      "essageH\000\022A\n\017command_message\030\005 \001(\0132&.org." +
      "ethereum.protobuf.tcp.TransactionH\000\022N\n\024t" +
      "ransactions_message\030\006 \001(\0132..org.ethereum" +
      ".protobuf.tcp.TransactionsMessageH\000\"M\n\004T" +
      "ype\022\n\n\006STATUS\020\000\022\016\n\nGET_BLOCKS\020\001\022\n\n\006BLOCK" +
      "S\020\002\022\013\n\007COMMAND\020\003\022\020\n\014TRANSACTIONS\020\004B\t\n\007da" +
      "taMsg\"\223\002\n\005Block\022\017\n\007version\030\001 \001(\r\022\027\n\017hash" +
      "_prev_block\030\002 \001(\014\022\030\n\020hash_merkle_root\030\003 " +
      "\001(\014\022\031\n\021hash_merkle_state\030\004 \001(\014\022\034\n\024hash_m" +
      "erkle_incubate\030\005 \001(\014\022\016\n\006height\030\006 \001(\r\022\022\n\n" +
      "created_at\030\007 \001(\r\022\016\n\006n_bits\030\010 \001(\014\022\r\n\005nonc" +
      "e\030\t \001(\014\022\024\n\014block_notice\030\n \001(\014\0224\n\004body\030\013 " +
      "\003(\0132&.org.ethereum.protobuf.tcp.Transact" +
      "ion\"\202\004\n\013Transaction\022\017\n\007version\030\001 \001(\r\022\014\n\004" +
      "hash\030\002 \001(\014\0229\n\004type\030\003 \001(\0162+.org.ethereum." +
      "protobuf.tcp.Transaction.Type\022\r\n\005nonce\030\004" +
      " \001(\004\022\014\n\004from\030\005 \001(\014\022\021\n\tgas_price\030\006 \001(\004\022\016\n" +
      "\006amount\030\007 \001(\004\022\n\n\002to\030\010 \001(\014\022\021\n\tsignature\030\t" +
      " \001(\014\022\022\n\npayloadlen\030\n \001(\r\022\017\n\007payload\030\013 \001(" +
      "\014\"\224\002\n\004Type\022\014\n\010COINBASE\020\000\022\014\n\010TRANSFER\020\001\022\010" +
      "\n\004VOTE\020\002\022\013\n\007DEPOSIT\020\003\022\036\n\032TRANSFER_MULTIS" +
      "IG_MULTISIG\020\004\022\034\n\030TRANSFER_MULTISIG_NORMA" +
      "L\020\005\022\034\n\030TRANSFER_NORMAL_MULTISIG\020\006\022\020\n\014ASS" +
      "ET_DEFINE\020\007\022\023\n\017ATOMIC_EXCHANGE\020\010\022\014\n\010INCU" +
      "BATE\020\t\022\024\n\020EXTRACT_INTEREST\020\n\022\032\n\026EXTRACT_" +
      "SHARING_PROFIT\020\013\022\026\n\022TERMINATE_INCUBATE\020\014" +
      "\"\200\001\n\rStatusMessage\022\017\n\007version\030\001 \001(\r\022\026\n\016c" +
      "urrent_height\030\002 \001(\r\022\032\n\022current_block_has" +
      "h\030\003 \001(\014\022\024\n\014total_weight\030\004 \001(\r\022\024\n\014genesis" +
      "_hash\030\005 \001(\014\"V\n\021GetHeadersMessage\022\022\n\nhash" +
      "_count\030\001 \001(\r\022\032\n\022block_locator_hash\030\002 \001(\014" +
      "\022\021\n\thash_stop\030\003 \001(\014\"R\n\016HeadersMessage\022\r\n" +
      "\005count\030\001 \001(\r\0221\n\007headers\030\002 \003(\0132 .org.ethe" +
      "reum.protobuf.tcp.Block\"U\n\020GetBlocksMess" +
      "age\022\024\n\014start_height\030\001 \001(\r\022\023\n\013stop_height" +
      "\030\002 \001(\r\022\026\n\016clip_from_stop\030\003 \001(\010\"P\n\rBlocks" +
      "Message\022\r\n\005count\030\001 \001(\r\0220\n\006blocks\030\002 \003(\0132 " +
      ".org.ethereum.protobuf.tcp.Block\"\312\002\n\nP2P" +
      "Message\0228\n\004type\030\001 \001(\0162*.org.ethereum.pro" +
      "tobuf.tcp.P2PMessage.Type\022J\n\022disconnect_" +
      "message\030\002 \001(\0132,.org.ethereum.protobuf.tc" +
      "p.DisconnectMessageH\000\022>\n\014ping_message\030\003 " +
      "\001(\0132&.org.ethereum.protobuf.tcp.PingMess" +
      "ageH\000\022>\n\014pong_message\030\004 \001(\0132&.org.ethere" +
      "um.protobuf.tcp.PongMessageH\000\"*\n\004Type\022\016\n" +
      "\nDISCONNECT\020\000\022\010\n\004PING\020\001\022\010\n\004PONG\020\002B\n\n\010dat" +
      "a_msg\"(\n\021DisconnectMessage\022\023\n\013reason_cod" +
      "e\030\001 \001(\t\"\r\n\013PingMessage\"\r\n\013PongMessageB\021B" +
      "\rProtocolModelH\001b\006proto3"
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
    internal_static_org_ethereum_protobuf_tcp_Message_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_ethereum_protobuf_tcp_Message_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_Message_descriptor,
        new java.lang.String[] { "Type", "Answered", "LastTimestamp", "RetryTimes", "P2PMessage", "ProtocolMessage", "DataMsg", });
    internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_TransactionsMessage_descriptor,
        new java.lang.String[] { "Transaction", });
    internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_ProtocolMessage_descriptor,
        new java.lang.String[] { "Type", "StatusMessage", "GetBlocksMessage", "BlocksMessage", "CommandMessage", "TransactionsMessage", "DataMsg", });
    internal_static_org_ethereum_protobuf_tcp_Block_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_org_ethereum_protobuf_tcp_Block_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_Block_descriptor,
        new java.lang.String[] { "Version", "HashPrevBlock", "HashMerkleRoot", "HashMerkleState", "HashMerkleIncubate", "Height", "CreatedAt", "NBits", "Nonce", "BlockNotice", "Body", });
    internal_static_org_ethereum_protobuf_tcp_Transaction_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_org_ethereum_protobuf_tcp_Transaction_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_Transaction_descriptor,
        new java.lang.String[] { "Version", "Hash", "Type", "Nonce", "From", "GasPrice", "Amount", "To", "Signature", "Payloadlen", "Payload", });
    internal_static_org_ethereum_protobuf_tcp_StatusMessage_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_org_ethereum_protobuf_tcp_StatusMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_StatusMessage_descriptor,
        new java.lang.String[] { "Version", "CurrentHeight", "CurrentBlockHash", "TotalWeight", "GenesisHash", });
    internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_GetHeadersMessage_descriptor,
        new java.lang.String[] { "HashCount", "BlockLocatorHash", "HashStop", });
    internal_static_org_ethereum_protobuf_tcp_HeadersMessage_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_org_ethereum_protobuf_tcp_HeadersMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_HeadersMessage_descriptor,
        new java.lang.String[] { "Count", "Headers", });
    internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_GetBlocksMessage_descriptor,
        new java.lang.String[] { "StartHeight", "StopHeight", "ClipFromStop", });
    internal_static_org_ethereum_protobuf_tcp_BlocksMessage_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_org_ethereum_protobuf_tcp_BlocksMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_BlocksMessage_descriptor,
        new java.lang.String[] { "Count", "Blocks", });
    internal_static_org_ethereum_protobuf_tcp_P2PMessage_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_org_ethereum_protobuf_tcp_P2PMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_P2PMessage_descriptor,
        new java.lang.String[] { "Type", "DisconnectMessage", "PingMessage", "PongMessage", "DataMsg", });
    internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_DisconnectMessage_descriptor,
        new java.lang.String[] { "ReasonCode", });
    internal_static_org_ethereum_protobuf_tcp_PingMessage_descriptor =
      getDescriptor().getMessageTypes().get(12);
    internal_static_org_ethereum_protobuf_tcp_PingMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_PingMessage_descriptor,
        new java.lang.String[] { });
    internal_static_org_ethereum_protobuf_tcp_PongMessage_descriptor =
      getDescriptor().getMessageTypes().get(13);
    internal_static_org_ethereum_protobuf_tcp_PongMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_ethereum_protobuf_tcp_PongMessage_descriptor,
        new java.lang.String[] { });
  }

  // @@protoc_insertion_point(outer_class_scope)
}