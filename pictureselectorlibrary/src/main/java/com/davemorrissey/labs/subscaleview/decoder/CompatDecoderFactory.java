package com.davemorrissey.labs.subscaleview.decoder;

/**
 * Compatibility factory to instantiate decoders with empty public constructors.
 * @param <T> The base type of the decoder this factory will produce.
 */
public class CompatDecoderFactory <T> implements DecoderFactory<T> {
  private Class<? extends T> clazz;

  public CompatDecoderFactory(Class<? extends T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public T make() throws IllegalAccessException, InstantiationException {
    return clazz.newInstance();
  }
}
