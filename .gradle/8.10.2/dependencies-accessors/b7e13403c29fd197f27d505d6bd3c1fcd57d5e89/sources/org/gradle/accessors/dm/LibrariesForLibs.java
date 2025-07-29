package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final AndroidxLibraryAccessors laccForAndroidxLibraryAccessors = new AndroidxLibraryAccessors(owner);
    private final AwsLibraryAccessors laccForAwsLibraryAccessors = new AwsLibraryAccessors(owner);
    private final ConverterLibraryAccessors laccForConverterLibraryAccessors = new ConverterLibraryAccessors(owner);
    private final CoroutinesLibraryAccessors laccForCoroutinesLibraryAccessors = new CoroutinesLibraryAccessors(owner);
    private final HiltLibraryAccessors laccForHiltLibraryAccessors = new HiltLibraryAccessors(owner);
    private final JacksonLibraryAccessors laccForJacksonLibraryAccessors = new JacksonLibraryAccessors(owner);
    private final JtsLibraryAccessors laccForJtsLibraryAccessors = new JtsLibraryAccessors(owner);
    private final LifecycleLibraryAccessors laccForLifecycleLibraryAccessors = new LifecycleLibraryAccessors(owner);
    private final LoggingLibraryAccessors laccForLoggingLibraryAccessors = new LoggingLibraryAccessors(owner);
    private final MapboxLibraryAccessors laccForMapboxLibraryAccessors = new MapboxLibraryAccessors(owner);
    private final NavigationLibraryAccessors laccForNavigationLibraryAccessors = new NavigationLibraryAccessors(owner);
    private final Retrofit2LibraryAccessors laccForRetrofit2LibraryAccessors = new Retrofit2LibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Dependency provider for <b>glide</b> with <b>com.github.bumptech.glide:glide</b> coordinates and
     * with version reference <b>glide</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getGlide() {
        return create("glide");
    }

    /**
     * Dependency provider for <b>gson</b> with <b>com.google.code.gson:gson</b> coordinates and
     * with version reference <b>gson</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getGson() {
        return create("gson");
    }

    /**
     * Dependency provider for <b>junit</b> with <b>junit:junit</b> coordinates and
     * with version reference <b>junit</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJunit() {
        return create("junit");
    }

    /**
     * Dependency provider for <b>jwt</b> with <b>com.auth0.android:jwtdecode</b> coordinates and
     * with version reference <b>jwt</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJwt() {
        return create("jwt");
    }

    /**
     * Dependency provider for <b>material</b> with <b>com.google.android.material:material</b> coordinates and
     * with version reference <b>material</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getMaterial() {
        return create("material");
    }

    /**
     * Dependency provider for <b>retrofit</b> with <b>com.squareup.retrofit2:retrofit</b> coordinates and
     * with version reference <b>retrofit</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getRetrofit() {
        return create("retrofit");
    }

    /**
     * Group of libraries at <b>androidx</b>
     */
    public AndroidxLibraryAccessors getAndroidx() {
        return laccForAndroidxLibraryAccessors;
    }

    /**
     * Group of libraries at <b>aws</b>
     */
    public AwsLibraryAccessors getAws() {
        return laccForAwsLibraryAccessors;
    }

    /**
     * Group of libraries at <b>converter</b>
     */
    public ConverterLibraryAccessors getConverter() {
        return laccForConverterLibraryAccessors;
    }

    /**
     * Group of libraries at <b>coroutines</b>
     */
    public CoroutinesLibraryAccessors getCoroutines() {
        return laccForCoroutinesLibraryAccessors;
    }

    /**
     * Group of libraries at <b>hilt</b>
     */
    public HiltLibraryAccessors getHilt() {
        return laccForHiltLibraryAccessors;
    }

    /**
     * Group of libraries at <b>jackson</b>
     */
    public JacksonLibraryAccessors getJackson() {
        return laccForJacksonLibraryAccessors;
    }

    /**
     * Group of libraries at <b>jts</b>
     */
    public JtsLibraryAccessors getJts() {
        return laccForJtsLibraryAccessors;
    }

    /**
     * Group of libraries at <b>lifecycle</b>
     */
    public LifecycleLibraryAccessors getLifecycle() {
        return laccForLifecycleLibraryAccessors;
    }

    /**
     * Group of libraries at <b>logging</b>
     */
    public LoggingLibraryAccessors getLogging() {
        return laccForLoggingLibraryAccessors;
    }

    /**
     * Group of libraries at <b>mapbox</b>
     */
    public MapboxLibraryAccessors getMapbox() {
        return laccForMapboxLibraryAccessors;
    }

    /**
     * Group of libraries at <b>navigation</b>
     */
    public NavigationLibraryAccessors getNavigation() {
        return laccForNavigationLibraryAccessors;
    }

    /**
     * Group of libraries at <b>retrofit2</b>
     */
    public Retrofit2LibraryAccessors getRetrofit2() {
        return laccForRetrofit2LibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class AndroidxLibraryAccessors extends SubDependencyFactory {
        private final AndroidxCoreLibraryAccessors laccForAndroidxCoreLibraryAccessors = new AndroidxCoreLibraryAccessors(owner);
        private final AndroidxEspressoLibraryAccessors laccForAndroidxEspressoLibraryAccessors = new AndroidxEspressoLibraryAccessors(owner);
        private final AndroidxLifecycleLibraryAccessors laccForAndroidxLifecycleLibraryAccessors = new AndroidxLifecycleLibraryAccessors(owner);
        private final AndroidxRoomLibraryAccessors laccForAndroidxRoomLibraryAccessors = new AndroidxRoomLibraryAccessors(owner);
        private final AndroidxWorkLibraryAccessors laccForAndroidxWorkLibraryAccessors = new AndroidxWorkLibraryAccessors(owner);

        public AndroidxLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>activity</b> with <b>androidx.activity:activity</b> coordinates and
         * with version reference <b>activity</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getActivity() {
            return create("androidx.activity");
        }

        /**
         * Dependency provider for <b>appcompat</b> with <b>androidx.appcompat:appcompat</b> coordinates and
         * with version reference <b>appcompat</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAppcompat() {
            return create("androidx.appcompat");
        }

        /**
         * Dependency provider for <b>constraintlayout</b> with <b>androidx.constraintlayout:constraintlayout</b> coordinates and
         * with version reference <b>constraintlayout</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getConstraintlayout() {
            return create("androidx.constraintlayout");
        }

        /**
         * Dependency provider for <b>junit</b> with <b>androidx.test.ext:junit</b> coordinates and
         * with version reference <b>junitVersion</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJunit() {
            return create("androidx.junit");
        }

        /**
         * Dependency provider for <b>viewpager2</b> with <b>androidx.viewpager2:viewpager2</b> coordinates and
         * with version reference <b>viewpager2</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getViewpager2() {
            return create("androidx.viewpager2");
        }

        /**
         * Group of libraries at <b>androidx.core</b>
         */
        public AndroidxCoreLibraryAccessors getCore() {
            return laccForAndroidxCoreLibraryAccessors;
        }

        /**
         * Group of libraries at <b>androidx.espresso</b>
         */
        public AndroidxEspressoLibraryAccessors getEspresso() {
            return laccForAndroidxEspressoLibraryAccessors;
        }

        /**
         * Group of libraries at <b>androidx.lifecycle</b>
         */
        public AndroidxLifecycleLibraryAccessors getLifecycle() {
            return laccForAndroidxLifecycleLibraryAccessors;
        }

        /**
         * Group of libraries at <b>androidx.room</b>
         */
        public AndroidxRoomLibraryAccessors getRoom() {
            return laccForAndroidxRoomLibraryAccessors;
        }

        /**
         * Group of libraries at <b>androidx.work</b>
         */
        public AndroidxWorkLibraryAccessors getWork() {
            return laccForAndroidxWorkLibraryAccessors;
        }

    }

    public static class AndroidxCoreLibraryAccessors extends SubDependencyFactory {
        private final AndroidxCoreKtxLibraryAccessors laccForAndroidxCoreKtxLibraryAccessors = new AndroidxCoreKtxLibraryAccessors(owner);

        public AndroidxCoreLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>androidx.core.ktx</b>
         */
        public AndroidxCoreKtxLibraryAccessors getKtx() {
            return laccForAndroidxCoreKtxLibraryAccessors;
        }

    }

    public static class AndroidxCoreKtxLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public AndroidxCoreKtxLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>ktx</b> with <b>androidx.core:core-ktx</b> coordinates and
         * with version reference <b>coreKtx</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("androidx.core.ktx");
        }

        /**
         * Dependency provider for <b>v1101</b> with <b>androidx.core:core-ktx</b> coordinates and
         * with version reference <b>coreKtxVersion</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getV1101() {
            return create("androidx.core.ktx.v1101");
        }

    }

    public static class AndroidxEspressoLibraryAccessors extends SubDependencyFactory {

        public AndroidxEspressoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>androidx.test.espresso:espresso-core</b> coordinates and
         * with version reference <b>espressoCore</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("androidx.espresso.core");
        }

    }

    public static class AndroidxLifecycleLibraryAccessors extends SubDependencyFactory {
        private final AndroidxLifecycleLivedataLibraryAccessors laccForAndroidxLifecycleLivedataLibraryAccessors = new AndroidxLifecycleLivedataLibraryAccessors(owner);
        private final AndroidxLifecycleViewmodelLibraryAccessors laccForAndroidxLifecycleViewmodelLibraryAccessors = new AndroidxLifecycleViewmodelLibraryAccessors(owner);

        public AndroidxLifecycleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>androidx.lifecycle.livedata</b>
         */
        public AndroidxLifecycleLivedataLibraryAccessors getLivedata() {
            return laccForAndroidxLifecycleLivedataLibraryAccessors;
        }

        /**
         * Group of libraries at <b>androidx.lifecycle.viewmodel</b>
         */
        public AndroidxLifecycleViewmodelLibraryAccessors getViewmodel() {
            return laccForAndroidxLifecycleViewmodelLibraryAccessors;
        }

    }

    public static class AndroidxLifecycleLivedataLibraryAccessors extends SubDependencyFactory {

        public AndroidxLifecycleLivedataLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>ktx</b> with <b>androidx.lifecycle:lifecycle-livedata-ktx</b> coordinates and
         * with version reference <b>lifecycleViewmodelKtx</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKtx() {
            return create("androidx.lifecycle.livedata.ktx");
        }

    }

    public static class AndroidxLifecycleViewmodelLibraryAccessors extends SubDependencyFactory {

        public AndroidxLifecycleViewmodelLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>ktx</b> with <b>androidx.lifecycle:lifecycle-viewmodel-ktx</b> coordinates and
         * with version reference <b>lifecycleViewmodelKtx</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKtx() {
            return create("androidx.lifecycle.viewmodel.ktx");
        }

    }

    public static class AndroidxRoomLibraryAccessors extends SubDependencyFactory {

        public AndroidxRoomLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>compiler</b> with <b>androidx.room:room-compiler</b> coordinates and
         * with version reference <b>room</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCompiler() {
            return create("androidx.room.compiler");
        }

        /**
         * Dependency provider for <b>ktx</b> with <b>androidx.room:room-ktx</b> coordinates and
         * with version reference <b>room</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKtx() {
            return create("androidx.room.ktx");
        }

        /**
         * Dependency provider for <b>runtime</b> with <b>androidx.room:room-runtime</b> coordinates and
         * with version reference <b>room</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRuntime() {
            return create("androidx.room.runtime");
        }

    }

    public static class AndroidxWorkLibraryAccessors extends SubDependencyFactory {
        private final AndroidxWorkRuntimeLibraryAccessors laccForAndroidxWorkRuntimeLibraryAccessors = new AndroidxWorkRuntimeLibraryAccessors(owner);

        public AndroidxWorkLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>androidx.work.runtime</b>
         */
        public AndroidxWorkRuntimeLibraryAccessors getRuntime() {
            return laccForAndroidxWorkRuntimeLibraryAccessors;
        }

    }

    public static class AndroidxWorkRuntimeLibraryAccessors extends SubDependencyFactory {

        public AndroidxWorkRuntimeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>ktx</b> with <b>androidx.work:work-runtime-ktx</b> coordinates and
         * with version reference <b>workRuntimeKtx</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKtx() {
            return create("androidx.work.runtime.ktx");
        }

    }

    public static class AwsLibraryAccessors extends SubDependencyFactory {
        private final AwsAndroidLibraryAccessors laccForAwsAndroidLibraryAccessors = new AwsAndroidLibraryAccessors(owner);

        public AwsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>aws.android</b>
         */
        public AwsAndroidLibraryAccessors getAndroid() {
            return laccForAwsAndroidLibraryAccessors;
        }

    }

    public static class AwsAndroidLibraryAccessors extends SubDependencyFactory {
        private final AwsAndroidSdkLibraryAccessors laccForAwsAndroidSdkLibraryAccessors = new AwsAndroidSdkLibraryAccessors(owner);

        public AwsAndroidLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>aws.android.sdk</b>
         */
        public AwsAndroidSdkLibraryAccessors getSdk() {
            return laccForAwsAndroidSdkLibraryAccessors;
        }

    }

    public static class AwsAndroidSdkLibraryAccessors extends SubDependencyFactory {
        private final AwsAndroidSdkMobileLibraryAccessors laccForAwsAndroidSdkMobileLibraryAccessors = new AwsAndroidSdkMobileLibraryAccessors(owner);

        public AwsAndroidSdkLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>s3</b> with <b>com.amazonaws:aws-android-sdk-s3</b> coordinates and
         * with version reference <b>awsAndroidSdkS3</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getS3() {
            return create("aws.android.sdk.s3");
        }

        /**
         * Group of libraries at <b>aws.android.sdk.mobile</b>
         */
        public AwsAndroidSdkMobileLibraryAccessors getMobile() {
            return laccForAwsAndroidSdkMobileLibraryAccessors;
        }

    }

    public static class AwsAndroidSdkMobileLibraryAccessors extends SubDependencyFactory {

        public AwsAndroidSdkMobileLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>client</b> with <b>com.amazonaws:aws-android-sdk-mobile-client</b> coordinates and
         * with version reference <b>awsAndroidSdkS3</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getClient() {
            return create("aws.android.sdk.mobile.client");
        }

    }

    public static class ConverterLibraryAccessors extends SubDependencyFactory {

        public ConverterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>gson</b> with <b>com.squareup.retrofit2:converter-gson</b> coordinates and
         * with version reference <b>retrofit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGson() {
            return create("converter.gson");
        }

    }

    public static class CoroutinesLibraryAccessors extends SubDependencyFactory {

        public CoroutinesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>android</b> with <b>org.jetbrains.kotlinx:kotlinx-coroutines-android</b> coordinates and
         * with version reference <b>coroutines</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAndroid() {
            return create("coroutines.android");
        }

        /**
         * Dependency provider for <b>core</b> with <b>org.jetbrains.kotlinx:kotlinx-coroutines-core</b> coordinates and
         * with version reference <b>coroutines</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("coroutines.core");
        }

    }

    public static class HiltLibraryAccessors extends SubDependencyFactory {
        private final HiltAndroidLibraryAccessors laccForHiltAndroidLibraryAccessors = new HiltAndroidLibraryAccessors(owner);

        public HiltLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>hilt.android</b>
         */
        public HiltAndroidLibraryAccessors getAndroid() {
            return laccForHiltAndroidLibraryAccessors;
        }

    }

    public static class HiltAndroidLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public HiltAndroidLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>android</b> with <b>com.google.dagger:hilt-android</b> coordinates and
         * with version reference <b>hilt</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("hilt.android");
        }

        /**
         * Dependency provider for <b>compiler</b> with <b>com.google.dagger:hilt-android-compiler</b> coordinates and
         * with version reference <b>hilt</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCompiler() {
            return create("hilt.android.compiler");
        }

    }

    public static class JacksonLibraryAccessors extends SubDependencyFactory {
        private final JacksonModuleLibraryAccessors laccForJacksonModuleLibraryAccessors = new JacksonModuleLibraryAccessors(owner);

        public JacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>databind</b> with <b>com.fasterxml.jackson.core:jackson-databind</b> coordinates and
         * with version reference <b>jacksonDatabind</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getDatabind() {
            return create("jackson.databind");
        }

        /**
         * Group of libraries at <b>jackson.module</b>
         */
        public JacksonModuleLibraryAccessors getModule() {
            return laccForJacksonModuleLibraryAccessors;
        }

    }

    public static class JacksonModuleLibraryAccessors extends SubDependencyFactory {

        public JacksonModuleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>kotlin</b> with <b>com.fasterxml.jackson.module:jackson-module-kotlin</b> coordinates and
         * with version reference <b>jacksonModuleKotlin</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKotlin() {
            return create("jackson.module.kotlin");
        }

    }

    public static class JtsLibraryAccessors extends SubDependencyFactory {

        public JtsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>org.locationtech.jts:jts-core</b> coordinates and
         * with version reference <b>jtsCore</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("jts.core");
        }

    }

    public static class LifecycleLibraryAccessors extends SubDependencyFactory {

        public LifecycleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>livedata</b> with <b>androidx.lifecycle:lifecycle-livedata-ktx</b> coordinates and
         * with version reference <b>lifecycle</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLivedata() {
            return create("lifecycle.livedata");
        }

        /**
         * Dependency provider for <b>viewmodel</b> with <b>androidx.lifecycle:lifecycle-viewmodel-ktx</b> coordinates and
         * with version reference <b>lifecycle</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getViewmodel() {
            return create("lifecycle.viewmodel");
        }

    }

    public static class LoggingLibraryAccessors extends SubDependencyFactory {

        public LoggingLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>interceptor</b> with <b>com.squareup.okhttp3:logging-interceptor</b> coordinates and
         * with version reference <b>loggingInterceptor</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getInterceptor() {
            return create("logging.interceptor");
        }

    }

    public static class MapboxLibraryAccessors extends SubDependencyFactory {
        private final MapboxSdkLibraryAccessors laccForMapboxSdkLibraryAccessors = new MapboxSdkLibraryAccessors(owner);

        public MapboxLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>mapbox.sdk</b>
         */
        public MapboxSdkLibraryAccessors getSdk() {
            return laccForMapboxSdkLibraryAccessors;
        }

    }

    public static class MapboxSdkLibraryAccessors extends SubDependencyFactory {

        public MapboxSdkLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>geojson</b> with <b>com.mapbox.mapboxsdk:mapbox-sdk-geojson</b> coordinates and
         * with version reference <b>mapboxSdkGeojson</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGeojson() {
            return create("mapbox.sdk.geojson");
        }

    }

    public static class NavigationLibraryAccessors extends SubDependencyFactory {

        public NavigationLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>compose</b> with <b>androidx.navigation:navigation-compose</b> coordinates and
         * with version reference <b>navigation</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCompose() {
            return create("navigation.compose");
        }

        /**
         * Dependency provider for <b>fragment</b> with <b>androidx.navigation:navigation-fragment-ktx</b> coordinates and
         * with version reference <b>navigation</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getFragment() {
            return create("navigation.fragment");
        }

        /**
         * Dependency provider for <b>ui</b> with <b>androidx.navigation:navigation-ui-ktx</b> coordinates and
         * with version reference <b>navigation</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getUi() {
            return create("navigation.ui");
        }

    }

    public static class Retrofit2LibraryAccessors extends SubDependencyFactory {
        private final Retrofit2ConverterLibraryAccessors laccForRetrofit2ConverterLibraryAccessors = new Retrofit2ConverterLibraryAccessors(owner);

        public Retrofit2LibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>retrofit2.converter</b>
         */
        public Retrofit2ConverterLibraryAccessors getConverter() {
            return laccForRetrofit2ConverterLibraryAccessors;
        }

    }

    public static class Retrofit2ConverterLibraryAccessors extends SubDependencyFactory {

        public Retrofit2ConverterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>simplexml</b> with <b>com.squareup.retrofit2:converter-simplexml</b> coordinates and
         * with version reference <b>converterSimplexml</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSimplexml() {
            return create("retrofit2.converter.simplexml");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>activity</b> with value <b>1.10.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getActivity() { return getVersion("activity"); }

        /**
         * Version alias <b>agp</b> with value <b>8.8.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAgp() { return getVersion("agp"); }

        /**
         * Version alias <b>appcompat</b> with value <b>1.7.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAppcompat() { return getVersion("appcompat"); }

        /**
         * Version alias <b>awsAndroidSdkS3</b> with value <b>2.66.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAwsAndroidSdkS3() { return getVersion("awsAndroidSdkS3"); }

        /**
         * Version alias <b>constraintlayout</b> with value <b>2.2.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getConstraintlayout() { return getVersion("constraintlayout"); }

        /**
         * Version alias <b>converterSimplexml</b> with value <b>2.9.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getConverterSimplexml() { return getVersion("converterSimplexml"); }

        /**
         * Version alias <b>coreKtx</b> with value <b>1.15.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCoreKtx() { return getVersion("coreKtx"); }

        /**
         * Version alias <b>coreKtxVersion</b> with value <b>1.10.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCoreKtxVersion() { return getVersion("coreKtxVersion"); }

        /**
         * Version alias <b>coroutines</b> with value <b>1.7.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCoroutines() { return getVersion("coroutines"); }

        /**
         * Version alias <b>espressoCore</b> with value <b>3.6.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getEspressoCore() { return getVersion("espressoCore"); }

        /**
         * Version alias <b>glide</b> with value <b>4.16.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGlide() { return getVersion("glide"); }

        /**
         * Version alias <b>gson</b> with value <b>2.10.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGson() { return getVersion("gson"); }

        /**
         * Version alias <b>hilt</b> with value <b>2.51.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getHilt() { return getVersion("hilt"); }

        /**
         * Version alias <b>jacksonDatabind</b> with value <b>2.13.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJacksonDatabind() { return getVersion("jacksonDatabind"); }

        /**
         * Version alias <b>jacksonModuleKotlin</b> with value <b>2.13.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJacksonModuleKotlin() { return getVersion("jacksonModuleKotlin"); }

        /**
         * Version alias <b>jtsCore</b> with value <b>1.18.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJtsCore() { return getVersion("jtsCore"); }

        /**
         * Version alias <b>junit</b> with value <b>4.13.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJunit() { return getVersion("junit"); }

        /**
         * Version alias <b>junitVersion</b> with value <b>1.2.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJunitVersion() { return getVersion("junitVersion"); }

        /**
         * Version alias <b>jwt</b> with value <b>2.0.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJwt() { return getVersion("jwt"); }

        /**
         * Version alias <b>kotlin</b> with value <b>1.9.22</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getKotlin() { return getVersion("kotlin"); }

        /**
         * Version alias <b>ksp</b> with value <b>1.9.22-1.0.17</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getKsp() { return getVersion("ksp"); }

        /**
         * Version alias <b>lifecycle</b> with value <b>2.8.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLifecycle() { return getVersion("lifecycle"); }

        /**
         * Version alias <b>lifecycleViewmodelKtx</b> with value <b>2.8.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLifecycleViewmodelKtx() { return getVersion("lifecycleViewmodelKtx"); }

        /**
         * Version alias <b>loggingInterceptor</b> with value <b>4.9.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLoggingInterceptor() { return getVersion("loggingInterceptor"); }

        /**
         * Version alias <b>mapboxSdkGeojson</b> with value <b>6.9.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMapboxSdkGeojson() { return getVersion("mapboxSdkGeojson"); }

        /**
         * Version alias <b>material</b> with value <b>1.12.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMaterial() { return getVersion("material"); }

        /**
         * Version alias <b>navigation</b> with value <b>2.8.4</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getNavigation() { return getVersion("navigation"); }

        /**
         * Version alias <b>retrofit</b> with value <b>2.9.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRetrofit() { return getVersion("retrofit"); }

        /**
         * Version alias <b>room</b> with value <b>2.6.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRoom() { return getVersion("room"); }

        /**
         * Version alias <b>viewpager2</b> with value <b>1.1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getViewpager2() { return getVersion("viewpager2"); }

        /**
         * Version alias <b>workRuntimeKtx</b> with value <b>2.10.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getWorkRuntimeKtx() { return getVersion("workRuntimeKtx"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {
        private final AndroidPluginAccessors paccForAndroidPluginAccessors = new AndroidPluginAccessors(providers, config);
        private final KotlinPluginAccessors paccForKotlinPluginAccessors = new KotlinPluginAccessors(providers, config);

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>hilt</b> with plugin id <b>com.google.dagger.hilt.android</b> and
         * with version reference <b>hilt</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getHilt() { return createPlugin("hilt"); }

        /**
         * Plugin provider for <b>ksp</b> with plugin id <b>com.google.devtools.ksp</b> and
         * with version reference <b>ksp</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getKsp() { return createPlugin("ksp"); }

        /**
         * Group of plugins at <b>plugins.android</b>
         */
        public AndroidPluginAccessors getAndroid() {
            return paccForAndroidPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.kotlin</b>
         */
        public KotlinPluginAccessors getKotlin() {
            return paccForKotlinPluginAccessors;
        }

    }

    public static class AndroidPluginAccessors extends PluginFactory {

        public AndroidPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>android.application</b> with plugin id <b>com.android.application</b> and
         * with version reference <b>agp</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getApplication() { return createPlugin("android.application"); }

    }

    public static class KotlinPluginAccessors extends PluginFactory {

        public KotlinPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>kotlin.android</b> with plugin id <b>org.jetbrains.kotlin.android</b> and
         * with version reference <b>kotlin</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getAndroid() { return createPlugin("kotlin.android"); }

    }

}
