package org.wisdom.p2p;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.9.0)",
    comments = "Source: wisdom.proto")
public final class WisdomGrpc {

  private WisdomGrpc() {}

  public static final String SERVICE_NAME = "Wisdom";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getEntryMethod()} instead. 
  public static final io.grpc.MethodDescriptor<org.wisdom.p2p.WisdomOuterClass.Message,
      org.wisdom.p2p.WisdomOuterClass.Message> METHOD_ENTRY = getEntryMethod();

  private static volatile io.grpc.MethodDescriptor<org.wisdom.p2p.WisdomOuterClass.Message,
      org.wisdom.p2p.WisdomOuterClass.Message> getEntryMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<org.wisdom.p2p.WisdomOuterClass.Message,
      org.wisdom.p2p.WisdomOuterClass.Message> getEntryMethod() {
    io.grpc.MethodDescriptor<org.wisdom.p2p.WisdomOuterClass.Message, org.wisdom.p2p.WisdomOuterClass.Message> getEntryMethod;
    if ((getEntryMethod = WisdomGrpc.getEntryMethod) == null) {
      synchronized (WisdomGrpc.class) {
        if ((getEntryMethod = WisdomGrpc.getEntryMethod) == null) {
          WisdomGrpc.getEntryMethod = getEntryMethod = 
              io.grpc.MethodDescriptor.<org.wisdom.p2p.WisdomOuterClass.Message, org.wisdom.p2p.WisdomOuterClass.Message>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Wisdom", "Entry"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.wisdom.p2p.WisdomOuterClass.Message.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.wisdom.p2p.WisdomOuterClass.Message.getDefaultInstance()))
                  .setSchemaDescriptor(new WisdomMethodDescriptorSupplier("Entry"))
                  .build();
          }
        }
     }
     return getEntryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WisdomStub newStub(io.grpc.Channel channel) {
    return new WisdomStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WisdomBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new WisdomBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WisdomFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new WisdomFutureStub(channel);
  }

  /**
   */
  public static abstract class WisdomImplBase implements io.grpc.BindableService {

    /**
     */
    public void entry(org.wisdom.p2p.WisdomOuterClass.Message request,
        io.grpc.stub.StreamObserver<org.wisdom.p2p.WisdomOuterClass.Message> responseObserver) {
      asyncUnimplementedUnaryCall(getEntryMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getEntryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.wisdom.p2p.WisdomOuterClass.Message,
                org.wisdom.p2p.WisdomOuterClass.Message>(
                  this, METHODID_ENTRY)))
          .build();
    }
  }

  /**
   */
  public static final class WisdomStub extends io.grpc.stub.AbstractStub<WisdomStub> {
    private WisdomStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WisdomStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WisdomStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WisdomStub(channel, callOptions);
    }

    /**
     */
    public void entry(org.wisdom.p2p.WisdomOuterClass.Message request,
        io.grpc.stub.StreamObserver<org.wisdom.p2p.WisdomOuterClass.Message> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getEntryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class WisdomBlockingStub extends io.grpc.stub.AbstractStub<WisdomBlockingStub> {
    private WisdomBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WisdomBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WisdomBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WisdomBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.wisdom.p2p.WisdomOuterClass.Message entry(org.wisdom.p2p.WisdomOuterClass.Message request) {
      return blockingUnaryCall(
          getChannel(), getEntryMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class WisdomFutureStub extends io.grpc.stub.AbstractStub<WisdomFutureStub> {
    private WisdomFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WisdomFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WisdomFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WisdomFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.wisdom.p2p.WisdomOuterClass.Message> entry(
        org.wisdom.p2p.WisdomOuterClass.Message request) {
      return futureUnaryCall(
          getChannel().newCall(getEntryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ENTRY = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final WisdomImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(WisdomImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ENTRY:
          serviceImpl.entry((org.wisdom.p2p.WisdomOuterClass.Message) request,
              (io.grpc.stub.StreamObserver<org.wisdom.p2p.WisdomOuterClass.Message>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class WisdomBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WisdomBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.wisdom.p2p.WisdomOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Wisdom");
    }
  }

  private static final class WisdomFileDescriptorSupplier
      extends WisdomBaseDescriptorSupplier {
    WisdomFileDescriptorSupplier() {}
  }

  private static final class WisdomMethodDescriptorSupplier
      extends WisdomBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    WisdomMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (WisdomGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WisdomFileDescriptorSupplier())
              .addMethod(getEntryMethod())
              .build();
        }
      }
    }
    return result;
  }
}
