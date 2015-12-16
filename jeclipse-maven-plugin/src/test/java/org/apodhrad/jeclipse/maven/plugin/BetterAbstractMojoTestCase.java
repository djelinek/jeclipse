package org.apodhrad.jeclipse.maven.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.sisu.plexus.CompositeBeanHelper;

/**
 * Source: http://stackoverflow.com/questions/9496534/test-default-values-and-
 * expressions-of-mojos-using-maven-plugin-testing-harness
 * 
 * Use this as you would {@link AbstractMojoTestCase}, where you want more of
 * the standard maven defaults to be set (and where the
 * {@link AbstractMojoTestCase} leaves them as null or empty). This includes:
 * <li>local repo, repo sessions and managers configured
 * <li>maven default remote repos installed (NB: this does not use your ~/.m2
 * local settings)
 * <li>system properties are copies
 * <p>
 * No changes to subclass code is needed; this simply intercepts the
 * {@link #newMavenSession(MavenProject)} method used by the various
 * {@link #lookupMojo(String, File)} methods.
 * <p>
 * This also provides new methods, {@link #newMavenSession()} to conveniently
 * create a maven session, and {@link #lookupConfiguredMojo(File, String)} so
 * you don't have to always build the project yourself.
 */
public abstract class BetterAbstractMojoTestCase extends AbstractMojoTestCase {

	private ComponentConfigurator configurator;

	protected MavenSession newMavenSession() {
		try {
			MavenExecutionRequest request = new DefaultMavenExecutionRequest();
			MavenExecutionResult result = new DefaultMavenExecutionResult();

			// populate sensible defaults, including repository basedir and
			// remote repos
			MavenExecutionRequestPopulator populator;
			populator = getContainer().lookup(MavenExecutionRequestPopulator.class);
			populator.populateDefaults(request);

			// this is needed to allow java profiles to get resolved; i.e. avoid
			// during project builds:
			// [ERROR] Failed to determine Java version for profile
			// java-1.5-detected @ org.apache.commons:commons-parent:22,
			// /Users/alex/.m2/repository/org/apache/commons/commons-parent/22/commons-parent-22.pom,
			// line 909, column 14
			request.setSystemProperties(System.getProperties());

			// and this is needed so that the repo session in the maven session
			// has a repo manager, and it points at the local repo
			// (cf MavenRepositorySystemUtils.newSession() which is what is
			// otherwise done)
			DefaultMaven maven = (DefaultMaven) getContainer().lookup(Maven.class);
			DefaultRepositorySystemSession repoSession = (DefaultRepositorySystemSession) maven
					.newRepositorySession(request);
			repoSession.setLocalRepositoryManager(new SimpleLocalRepositoryManagerFactory().newInstance(repoSession,
					new LocalRepository(request.getLocalRepository().getBasedir())));

			MavenSession session = new MavenSession(getContainer(), repoSession, request, result);
			return session;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Extends the super to use the new {@link #newMavenSession()} introduced
	 * here which sets the defaults one expects from maven; the standard test
	 * case leaves a lot of things blank
	 */
	@Override
	protected MavenSession newMavenSession(MavenProject project) {
		MavenSession session = newMavenSession();
		session.setCurrentProject(project);
		session.setProjects(Arrays.asList(project));
		return session;
	}

	/**
	 * As {@link #lookupConfiguredMojo(MavenProject, String)} but taking the pom
	 * file and creating the {@link MavenProject}.
	 */
	protected Mojo lookupConfiguredMojo(String goal, File pom) throws Exception {
		assertNotNull(pom);
		assertTrue(pom.exists());

		ProjectBuildingRequest buildingRequest = newMavenSession().getProjectBuildingRequest();
		ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
		MavenProject project = projectBuilder.build(pom, buildingRequest).getProject();

		return lookupConfiguredMojo(project, goal);
	}

	protected Mojo lookupConfiguredMojo(MavenSession session, MojoExecution execution)
			throws Exception, ComponentConfigurationException {
		configurator = getContainer().lookup(ComponentConfigurator.class, "basic");

		MavenProject project = session.getCurrentProject();
		MojoDescriptor mojoDescriptor = execution.getMojoDescriptor();

		Mojo mojo = (Mojo) lookup(mojoDescriptor.getRole(), mojoDescriptor.getRoleHint());

		ExpressionEvaluator evaluator = new PluginParameterExpressionEvaluator(session, execution);

		Xpp3Dom configuration = null;
		Plugin plugin = project.getPlugin(mojoDescriptor.getPluginDescriptor().getPluginLookupKey());
		if (plugin != null) {
			configuration = (Xpp3Dom) plugin.getConfiguration();
		}
		if (configuration == null) {
			configuration = new Xpp3Dom("configuration");
		}
		configuration = Xpp3Dom.mergeXpp3Dom(configuration, execution.getConfiguration());

		PlexusConfiguration pluginConfiguration = new XmlPlexusConfiguration(configuration);

		if (mojoDescriptor.getComponentConfigurator() != null) {
			configurator = getContainer().lookup(ComponentConfigurator.class,
					mojoDescriptor.getComponentConfigurator());
		}
		
		new MyComponentConfigurator(configurator).configureComponent(mojo, pluginConfiguration, evaluator,
				getContainer().getContainerRealm(), mojoDescriptor);

		return mojo;
	}

	private class MyComponentConfigurator extends BasicComponentConfigurator implements ComponentConfigurator {

		private ComponentConfigurator originalConfigurator;

		public MyComponentConfigurator(ComponentConfigurator originalConfigurator) {
			this.originalConfigurator = originalConfigurator;
		}

		@Override
		public void configureComponent(Object component, PlexusConfiguration configuration, ClassRealm realm)
				throws ComponentConfigurationException {
			originalConfigurator.configureComponent(component, configuration, realm);
		}

		@Override
		public void configureComponent(Object component, PlexusConfiguration configuration,
				ExpressionEvaluator evaluator, ClassRealm realm) throws ComponentConfigurationException {
			originalConfigurator.configureComponent(component, configuration, evaluator, realm);
		}
		
		public void configureComponent(Object component, PlexusConfiguration configuration,
				ExpressionEvaluator evaluator, ClassRealm realm, MojoDescriptor mojoDescriptor) throws ComponentConfigurationException {
			try {
				ClassRealmConverter.pushContextRealm(realm);

				new ObjectWithFieldsConverter().processConfiguration(converterLookup, component, realm, //
						configuration, evaluator, null, mojoDescriptor);
			} finally {
				ClassRealmConverter.popContextRealm();
			}
		}

		@Override
		public int hashCode() {
			return originalConfigurator.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return originalConfigurator.equals(obj);
		}

		@Override
		public String toString() {
			return originalConfigurator.toString();
		}

	}

	public class ObjectWithFieldsConverter extends AbstractConfigurationConverter {
		public boolean canConvert(final Class<?> type) {
			return !Map.class.isAssignableFrom(type) //
					&& !Collection.class.isAssignableFrom(type) //
					&& !Dictionary.class.isAssignableFrom(type);
		}

		public Object fromConfiguration(final ConverterLookup lookup, final PlexusConfiguration configuration,
				final Class<?> type, final Class<?> enclosingType, final ClassLoader loader,
				final ExpressionEvaluator evaluator, final ConfigurationListener listener)
						throws ComponentConfigurationException {
			final Object value = fromExpression(configuration, evaluator);
			if (type.isInstance(value)) {
				return value;
			}
			try {
				final Class<?> implType = getClassForImplementationHint(type, configuration, loader);
				if (null == value && implType.isInterface() && configuration.getChildCount() == 0) {
					return null; // nothing to process
				}
				final Object bean = instantiateObject(implType);
				if (null == value) {
					processConfiguration(lookup, bean, loader, configuration, evaluator, listener);
				} else {
					new CompositeBeanHelper(lookup, loader, evaluator, listener).setDefault(bean, value, configuration);
				}
				return bean;
			} catch (final ComponentConfigurationException e) {
				if (null == e.getFailedConfiguration()) {
					e.setFailedConfiguration(configuration);
				}
				throw e;
			}
		}

		public void processConfiguration(final ConverterLookup lookup, final Object bean, final ClassLoader loader,
				final PlexusConfiguration configuration, final ExpressionEvaluator evaluator)
						throws ComponentConfigurationException {
			processConfiguration(lookup, bean, loader, configuration, evaluator, null);
		}

		public void processConfiguration(final ConverterLookup lookup, final Object bean, final ClassLoader loader,
				final PlexusConfiguration configuration, final ExpressionEvaluator evaluator,
				final ConfigurationListener listener) throws ComponentConfigurationException {
			processConfiguration(lookup, bean, loader, configuration, evaluator, listener, null);
		}
		
		public void processConfiguration(final ConverterLookup lookup, final Object bean, final ClassLoader loader,
				final PlexusConfiguration configuration, final ExpressionEvaluator evaluator,
				final ConfigurationListener listener, MojoDescriptor mojoDescriptor) throws ComponentConfigurationException {
			final CompositeBeanHelper helper = new CompositeBeanHelper(lookup, loader, evaluator, listener);
			for (int i = 0, size = configuration.getChildCount(); i < size; i++) {
				final PlexusConfiguration element = configuration.getChild(i);
				String propertyName = fromXML(element.getName());
				
				if (mojoDescriptor != null) {
					List<Parameter> params = mojoDescriptor.getParameters();
					for (Parameter param: params) {
						if (param.getAlias() != null && param.getAlias().equals(propertyName)) {
							propertyName = param.getName();
						}
					}
				}
				
				Class<?> valueType;
				try {
					valueType = getClassForImplementationHint(null, element, loader);
				} catch (final ComponentConfigurationException e) {
					valueType = null;
				}
				helper.setProperty(bean, propertyName, valueType, element);
			}
		}
	}

}
