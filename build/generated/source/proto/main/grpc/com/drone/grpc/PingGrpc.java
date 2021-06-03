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
public final class PingGrpc {

  private PingGrpc() {}

  public static final String SERVICE_NAME = "com.drone.grpc.Ping";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.drone.grpc.DroneService.PingRequest,
      com.drone.grpc.DroneService.PingResponse> getAliveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Alive",
      requestType = com.drone.grpc.DroneService.PingRequest.class,
      responseType = com.drone.grpc.DroneService.PingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.drone.grpc.DroneService.PingRequest,
      com.drone.grpc.DroneService.PingResponse> getAliveMethod() {
    io.grpc.MethodDescriptor<com.drone.grpc.DroneService.PingRequest, com.drone.grpc.DroneService.PingResponse> getAliveMethod;
    if ((getAliveMethod = PingGrpc.getAliveMethod) == null) {
      synchronized (PingGrpc.class) {
        if ((getAliveMethod = PingGrpc.getAliveMethod) == null) {
          PingGrpc.getAliveMethod = getAliveMethod =
              io.grpc.MethodDescriptor.<com.drone.grpc.DroneService.PingRequest, com.drone.grpc.DroneService.PingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Alive"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.drone.grpc.DroneService.PingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.drone.grpc.DroneService.PingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PingMethodDescriptorSupplier("Alive"))
              .build();
        }
      }
    }
    return getAliveMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PingStub newStub(io.grpc.Channel channel) {
    return new PingStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PingBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new PingBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PingFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new PingFutureStub(channel);
  }

  /**
   */
  public static abstract class PingImplBase implements io.grpc.BindableService {

    /**
     */
    public void alive(com.drone.grpc.DroneService.PingRequest request,
        io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.PingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAliveMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAliveMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.drone.grpc.DroneService.PingRequest,
                com.drone.grpc.DroneService.PingResponse>(
                  this, METHODID_ALIVE)))
          .build();
    }
  }

  /**
   */
  public static final class PingStub extends io.grpc.stub.AbstractStub<PingStub> {
    private PingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PingStub(channel, callOptions);
    }

    /**
     */
    public void alive(com.drone.grpc.DroneService.PingRequest request,
        io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.PingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAliveMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PingBlockingStub extends io.grpc.stub.AbstractStub<PingBlockingStub> {
    private PingBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PingBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PingBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PingBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.drone.grpc.DroneService.PingResponse alive(com.drone.grpc.DroneService.PingRequest request) {
      return blockingUnaryCall(
          getChannel(), getAliveMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PingFutureStub extends io.grpc.stub.AbstractStub<PingFutureStub> {
    private PingFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PingFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PingFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PingFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.drone.grpc.DroneService.PingResponse> alive(
        com.drone.grpc.DroneService.PingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAliveMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ALIVE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PingImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PingImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ALIVE:
          serviceImpl.alive((com.drone.grpc.DroneService.PingRequest) request,
              (io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.PingResponse>) responseObserver);
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

  private static abstract class PingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PingBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.drone.grpc.DroneService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Ping");
    }
  }

  private static final class PingFileDescriptorSupplier
      extends PingBaseDescriptorSupplier {
    PingFileDescriptorSupplier() {}
  }

  private static final class PingMethodDescriptorSupplier
      extends PingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PingMethodDescriptorSupplier(String methodName) {
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
      synchronized (PingGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PingFileDescriptorSupplier())
              .addMethod(getAliveMethod())
              .build();
        }
      }
    }
    return result;
  }
}
