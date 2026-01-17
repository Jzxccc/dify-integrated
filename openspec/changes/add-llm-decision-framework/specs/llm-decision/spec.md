# LLM决策框架功能规范

## Purpose
本规范定义了LLM决策框架的需求，使系统能够接收用户输入，通过大语言模型决定需要执行的服务方法，实现智能化的服务调用。

## ADDED Requirements

### Requirement: 意图解析
系统SHALL parse user input to identify the intent and required parameters for service execution.

#### Scenario: 用户意图识别
给定一个用户的自然语言输入，
当LLM决策服务接收到输入时，
系统应分析输入以识别用户意图，
提取必要的参数，
确定最适合执行的服务。

#### Scenario: 参数提取
给定一个包含参数信息的用户输入，
当系统解析用户意图时，
系统应从输入中提取相关参数，
验证参数格式和类型，
准备用于服务调用的参数映射。

### Requirement: 服务决策
系统SHALL use LLM to decide which service to execute based on user input.

#### Scenario: LLM服务决策
给定用户意图和提取的参数，
当系统需要决定执行哪个服务时，
系统应调用LLM询问最适合的服务，
接收LLM的决策结果，
验证决策结果的合法性。

#### Scenario: 服务决策验证
给定LLM返回的服务决策，
当系统准备执行服务调用时，
系统应验证服务名称在注册表中存在，
验证参数符合服务Schema，
拒绝非法或不存在的服务调用。

### Requirement: 智能服务调用
系统SHALL execute the service determined by LLM decision with extracted parameters.

#### Scenario: 智能服务执行
给定LLM决策的服务名称和参数，
当系统执行服务调用时，
系统应使用IntelligentServiceInvoker执行服务，
传递从用户输入中提取的参数，
返回服务执行结果。

#### Scenario: 服务执行错误处理
给定LLM决策的服务调用失败，
当系统执行服务时，
系统应捕获和处理异常，
返回适当的错误信息，
记录错误日志。

### Requirement: LLM集成
系统SHALL provide interface for LLM communication.

#### Scenario: LLM客户端集成
给定需要与LLM通信的请求，
当系统需要获取服务决策时，
系统应通过LLMClient接口调用LLM，
传递用户输入和可用服务信息，
接收并解析LLM响应。

#### Scenario: LLM响应处理
给定LLM返回的响应，
当系统处理响应时，
系统应解析响应以提取服务名称和参数，
验证响应格式，
准备服务调用参数。

### Requirement: 服务映射
系统SHALL map user intents to registered AI services based on service schemas.

#### Scenario: 意图到服务映射
给定用户意图和参数，
当系统确定要执行的服务时，
系统应参考AIServiceRegistry中的服务列表，
根据服务描述和Schema匹配意图，
选择最合适的服务执行。

#### Scenario: 服务Schema使用
给定用户输入和可用服务Schema，
当系统解析用户意图时，
系统应使用服务Schema辅助参数提取，
确保提取的参数符合服务要求，
验证参数类型和必需性。