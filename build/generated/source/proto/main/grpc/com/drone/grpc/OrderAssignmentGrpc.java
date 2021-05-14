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
public final class OrderAssignmentGrpc {

  private OrderAssignmentGrpc() {}

  public static final String SERVICE_NAME = "com.drone.grpc.OrderAssignment";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.drone.grpc.DroneService.OrderRequest,
      com.drone.grpc.DroneService.OrderResponse> getAssignOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AssignOrder",
      requestType = com.drone.grpc.DroneService.OrderRequest.class,
      responseType = com.drone.grpc.DroneService.OrderResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.drone.grpc.DroneService.OrderRequest,
      com.drone.grpc.DroneService.OrderResponse> getAssignOrderMethod() {
    io.grpc.MethodDescriptor<com.drone.grpc.DroneService.OrderRequest, com.drone.grpc.DroneService.OrderResponse> getAssignOrderMethod;
    if ((getAssignOrderMethod = OrderAssignmentGrpc.getAssignOrderMethod) == null) {
      synchronized (OrderAssignmentGrpc.class) {
        if ((getAssignOrderMethod = OrderAssignmentGrpc.getAssignOrderMethod) == null) {
          OrderAssignmentGrpc.getAssignOrderMethod = getAssignOrderMethod =
              io.grpc.MethodDescriptor.<com.drone.grpc.DroneService.OrderRequest, com.drone.grpc.DroneService.OrderResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AssignOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.drone.grpc.DroneService.OrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.drone.grpc.DroneService.OrderResponse.getDefaultInstance()))
              .setSchemaDescriptor(new OrderAssignmentMethodDescriptorSupplier("AssignOrder"))
              .build();
        }
      }
    }
    return getAssignOrderMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static OrderAssignmentStub newStub(io.grpc.Channel channel) {
    return new OrderAssignmentStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static OrderAssignmentBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new OrderAssignmentBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static OrderAssignmentFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new OrderAssignmentFutureStub(channel);
  }

  /**
   */
  public static abstract class OrderAssignmentImplBase implements io.grpc.BindableService {

    /**
     */
    public void assignOrder(com.drone.grpc.DroneService.OrderRequest request,
        io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.OrderResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAssignOrderMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAssignOrderMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.drone.grpc.DroneService.OrderRequest,
                com.drone.grpc.DroneService.OrderResponse>(
                  this, METHODID_ASSIGN_ORDER)))
          .build();
    }
  }

  /**
   */
  public static final class OrderAssignmentStub extends io.grpc.stub.AbstractStub<OrderAssignmentStub> {
    private OrderAssignmentStub(io.grpc.Channel channel) {
      super(channel);
    }

    private OrderAssignmentStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderAssignmentStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OrderAssignmentStub(channel, callOptions);
    }

    /**
     */
    public void assignOrder(com.drone.grpc.DroneService.OrderRequest request,
        io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.OrderResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAssignOrderMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class OrderAssignmentBlockingStub extends io.grpc.stub.AbstractStub<OrderAssignmentBlockingStub> {
    private OrderAssignmentBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private OrderAssignmentBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderAssignmentBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OrderAssignmentBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.drone.grpc.DroneService.OrderResponse assignOrder(com.drone.grpc.DroneService.OrderRequest request) {
      return blockingUnaryCall(
          getChannel(), getAssignOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class OrderAssignmentFutureStub extends io.grpc.stub.AbstractStub<OrderAssignmentFutureStub> {
    private OrderAssignmentFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private OrderAssignmentFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderAssignmentFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OrderAssignmentFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.drone.grpc.DroneService.OrderResponse> assignOrder(
        com.drone.grpc.DroneService.OrderRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAssignOrderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ASSIGN_ORDER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final OrderAssignmentImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(OrderAssignmentImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ASSIGN_ORDER:
          serviceImpl.assignOrder((com.drone.grpc.DroneService.OrderRequest) request,
              (io.grpc.stub.StreamObserver<com.drone.grpc.DroneService.OrderResponse>) responseObserver);
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

  private static abstract class OrderAssignmentBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    OrderAssignmentBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.drone.grpc.DroneService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("OrderAssignment");
    }
  }

  private static final class OrderAssignmentFileDescriptorSupplier
      extends OrderAssignmentBaseDescriptorSupplier {
    OrderAssignmentFileDescriptorSupplier() {}
  }

  private static final class OrderAssignmentMethodDescriptorSupplier
      extends OrderAssignmentBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    OrderAssignmentMethodDescriptorSupplier(String methodName) {
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
      synchronized (OrderAssignmentGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new OrderAssignmentFileDescriptorSupplier())
              .addMethod(getAssignOrderMethod())
              .build();
        }
      }
    }
    return result;
  }
}
