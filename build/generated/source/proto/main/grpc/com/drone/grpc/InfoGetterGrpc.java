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
public final class InfoGetterGrpc {

  private InfoGetterGrpc() {}

  public static final String SERVICE_NAME = "com.drone.grpc.InfoGetter";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.drone.grpc.DroneService.InfoRequest,
      com.drone.grpc.DroneService.InfoResponse> getGetInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetInfo",
      requestType = com.drone.grpc.DroneService.InfoRequest.class,
      responseType = com.drone.grpc.DroneService.InfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.drone.grpc.DroneService.InfoRequest,
      com.drone.grpc.DroneService.InfoResponse> getGetInfoMethod() {
    io.grpc.MethodDescriptor<com.drone.grpc.DroneService.InfoRequest, com.drone.grpc.DroneService.InfoResponse> getGetInfoMethod;
    if ((getGetInfoMethod = InfoGetterGrpc.getGetInfoMethod) == null) {
      synchronized (InfoGetterGrpc.class) {
        if ((getGetInfoMethod = InfoGetterGrpc.getGetInfoMethod) == null) {
          InfoGetterGrpc.getGetInfoMethod = getGetInfoMethod =
              io.grpc.MethodDescriptor.<com.drone.grpc.DroneService.InfoRequest, com.drone.grpc.DroneService.InfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.drone.grpc.DroneService.InfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.drone.grpc.DroneService.InfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InfoGetterMethodDescriptorSupplier("GetInfo"))
              .build();
        }
      }
    }
    return getGetInfoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InfoGetterStub newStub(io.grpc.Channel channel) {
    return new InfoGetterStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InfoGetterBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new InfoGetterBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InfoGetterFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new InfoGetterFutureStub(channel);
  }

  /**
   */
  public static abstract class InfoGetterImplBase implements io.grpc.BindableService {

    /**
     */
    public void getInfo(com.drone.grpc.DroneService.InfoRequest request,
        io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.InfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetInfoMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.drone.grpc.DroneService.InfoRequest,
                com.drone.grpc.DroneService.InfoResponse>(
                  this, METHODID_GET_INFO)))
          .build();
    }
  }

  /**
   */
  public static final class InfoGetterStub extends io.grpc.stub.AbstractStub<InfoGetterStub> {
    private InfoGetterStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InfoGetterStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InfoGetterStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InfoGetterStub(channel, callOptions);
    }

    /**
     */
    public void getInfo(com.drone.grpc.DroneService.InfoRequest request,
        io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.InfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetInfoMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InfoGetterBlockingStub extends io.grpc.stub.AbstractStub<InfoGetterBlockingStub> {
    private InfoGetterBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InfoGetterBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InfoGetterBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InfoGetterBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.drone.grpc.DroneService.InfoResponse getInfo(com.drone.grpc.DroneService.InfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetInfoMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InfoGetterFutureStub extends io.grpc.stub.AbstractStub<InfoGetterFutureStub> {
    private InfoGetterFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InfoGetterFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InfoGetterFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InfoGetterFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.drone.grpc.DroneService.InfoResponse> getInfo(
        com.drone.grpc.DroneService.InfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetInfoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_INFO = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InfoGetterImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InfoGetterImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_INFO:
          serviceImpl.getInfo((com.drone.grpc.DroneService.InfoRequest) request,
              (io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.InfoResponse>) responseObserver);
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

  private static abstract class InfoGetterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InfoGetterBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.drone.grpc.DroneService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InfoGetter");
    }
  }

  private static final class InfoGetterFileDescriptorSupplier
      extends InfoGetterBaseDescriptorSupplier {
    InfoGetterFileDescriptorSupplier() {}
  }

  private static final class InfoGetterMethodDescriptorSupplier
      extends InfoGetterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InfoGetterMethodDescriptorSupplier(String methodName) {
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
      synchronized (InfoGetterGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InfoGetterFileDescriptorSupplier())
              .addMethod(getGetInfoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
