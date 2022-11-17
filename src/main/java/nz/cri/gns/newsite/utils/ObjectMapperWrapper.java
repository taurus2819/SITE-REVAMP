/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author scaddenp
 */
public enum ObjectMapperWrapper {

  INSTANCE;

  private final ObjectMapper mapper;

  private ObjectMapperWrapper() {
    this.mapper = create();
  }

  public ObjectMapper get() {
    return this.mapper;
  }

  private static ObjectMapper create() {
    ObjectMapper mapper = new ObjectMapper();
    return mapper;
  }

}