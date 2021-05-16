package com.drone.grpc;

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
    value = "by gRPC proto compiler (version 1.25.0)",
    comments = "Source: droneService.proto")
public final class InfoSenderGrpc {

  private InfoSenderGrpc() {}

  public static final String SERVICE_NAME = "com.drone.grpc.InfoSender";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.drone.grpc.DroneService.SenderInfoRequest,
      com.drone.grpc.DroneService.SenderInfoResponse> getSendInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendInfo",
      requestType = com.drone.grpc.DroneService.SenderInfoRequest.class,
      responseType = com.drone.grpc.DroneService.SenderInfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.drone.grpc.DroneService.SenderInfoRequest,
      com.drone.grpc.DroneService.SenderInfoResponse> getSendInfoMethod() {
    io.grpc.MethodDescriptor<com.drone.grpc.DroneService.SenderInfoRequest, com.drone.grpc.DroneService.SenderInfoResponse> getSendInfoMethod;
    if ((getSendInfoMethod = InfoSenderGrpc.getSendInfoMethod) == null) {
      synchronized (InfoSenderGrpc.class) {
        if ((getSendInfoMethod = InfoSenderGrpc.getSendInfoMethod) == null) {
          InfoSenderGrpc.getSendInfoMethod = getSendInfoMethod =
              io.grpc.MethodDescriptor.<com.drone.grpc.DroneService.SenderInfoRequest, com.drone.grpc.DroneService.SenderInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.drone.grpc.DroneService.SenderInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.drone.grpc.DroneService.SenderInfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InfoSenderMethodDescriptorSupplier("SendInfo"))
              .build();
        }
      }
    }
    return getSendInfoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InfoSenderStub newStub(io.grpc.Channel channel) {
    return new InfoSenderStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InfoSenderBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new InfoSenderBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InfoSenderFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new InfoSenderFutureStub(channel);
  }

  /**
   */
  public static abstract class InfoSenderImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendInfo(com.drone.grpc.DroneService.SenderInfoRequest request,
        io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.SenderInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSendInfoMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.drone.grpc.DroneService.SenderInfoRequest,
                com.drone.grpc.DroneService.SenderInfoResponse>(
                  this, METHODID_SEND_INFO)))
          .build();
    }
  }

  /**
   */
  public static final class InfoSenderStub extends io.grpc.stub.AbstractStub<InfoSenderStub> {
    private InfoSenderStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InfoSenderStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InfoSenderStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InfoSenderStub(channel, callOptions);
    }

    /**
     */
    public void sendInfo(com.drone.grpc.DroneService.SenderInfoRequest request,
        io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.SenderInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSendInfoMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InfoSenderBlockingStub extends io.grpc.stub.AbstractStub<InfoSenderBlockingStub> {
    private InfoSenderBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InfoSenderBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InfoSenderBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InfoSenderBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.drone.grpc.DroneService.SenderInfoResponse sendInfo(com.drone.grpc.DroneService.SenderInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getSendInfoMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InfoSenderFutureStub extends io.grpc.stub.AbstractStub<InfoSenderFutureStub> {
    private InfoSenderFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InfoSenderFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InfoSenderFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InfoSenderFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.drone.grpc.DroneService.SenderInfoResponse> sendInfo(
        com.drone.grpc.DroneService.SenderInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSendInfoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_INFO = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InfoSenderImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InfoSenderImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_INFO:
          serviceImpl.sendInfo((com.drone.grpc.DroneService.SenderInfoRequest) request,
              (io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.SenderInfoResponse>) responseObserver);
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

  private static abstract class InfoSenderBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InfoSenderBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.drone.grpc.DroneService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InfoSender");
    }
  }

  private static final class InfoSenderFileDescriptorSupplier
      extends InfoSenderBaseDescriptorSupplier {
    InfoSenderFileDescriptorSupplier() {}
  }

  private static final class InfoSenderMethodDescriptorSupplier
      extends InfoSenderBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InfoSenderMethodDescriptorSupplier(String methodName) {
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
      synchronized (InfoSenderGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InfoSenderFileDescriptorSupplier())
              .addMethod(getSendInfoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
