/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.osgi.bnd.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osmorc.i18n.OsmorcBundle;

import java.io.File;

public class BndRunConfiguration extends LocatableConfigurationBase implements ModuleRunProfile {
  public String bndRunFile = "";
  public boolean useAlternativeJre = false;
  public String alternativeJrePath = "";

  public BndRunConfiguration(Project project, @NotNull ConfigurationFactory factory, String name) {
    super(project, factory, name);
  }

  @Override
  public void readExternal(Element element) throws InvalidDataException {
    super.readExternal(element);
    XmlSerializer.deserializeInto(this, element);
  }

  @Override
  public void writeExternal(Element element) throws WriteExternalException {
    super.writeExternal(element);
    RunConfiguration template = getFactory().createTemplateConfiguration(getProject());
    XmlSerializer.serializeInto(this, element, new SkipDefaultValuesSerializationFilters(template));
  }

  @Override
  protected boolean isNewSerializationUsed() {
    return true;
  }

  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new BndRunConfigurationEditor(getProject());
  }

  @Nullable
  @Override
  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
    return new BndRunState(environment, this);
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    if (!new File(bndRunFile).isFile()) {
      throw new RuntimeConfigurationException(OsmorcBundle.message("bnd.run.configuration.invalid", bndRunFile));
    }
    if (useAlternativeJre) {
      JavaParametersUtil.checkAlternativeJRE(alternativeJrePath);
    }
  }

  @NotNull
  @Override
  public Module[] getModules() {
    return Module.EMPTY_ARRAY;
  }
}
