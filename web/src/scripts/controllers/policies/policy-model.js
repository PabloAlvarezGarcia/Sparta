(function () {
  'use strict';

  /*POLICY MODEL CONTROLLER*/
  angular
    .module('webApp')
    .controller('PolicyModelCtrl', PolicyModelCtrl);

  PolicyModelCtrl.$inject = ['ModelFactory', 'PolicyModelFactory', 'ModelStaticDataFactory', 'PolicyStaticDataFactory'];

  function PolicyModelCtrl(ModelFactory, PolicyModelFactory, ModelStaticDataFactory, PolicyStaticDataFactory) {
    var vm = this;
    vm.init = init;
    vm.changeDefaultConfiguration = changeDefaultConfiguration;

    vm.init();

    function init(model) {
      vm.showModelError = false;
      vm.configPlaceholder = PolicyStaticDataFactory.getConfigPlaceholder();
      vm.outputPattern = ModelStaticDataFactory.getOutputPattern();
      vm.outputInputPlaceholder = ModelStaticDataFactory.getOutputInputPlaceholder();
      vm.templateModelData = ModelStaticDataFactory;
      vm.modelError = ModelFactory.getError();

      if (model) {
        vm.model = model;
      }
      else {
        vm.model = ModelFactory.getModel();
        changeDefaultConfiguration();
      }
      vm.policy = PolicyModelFactory.getCurrentPolicy();
      vm.modelTypes = ModelStaticDataFactory.getTypes();
    }

    function changeDefaultConfiguration() {
      var configString = JSON.stringify(ModelStaticDataFactory.getDefaultConfigurations(vm.model.type),null, 4);
      vm.model.configuration = configString;
    }
  }
})();