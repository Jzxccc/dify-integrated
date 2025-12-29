# OpenSpec 使用指南

使用 OpenSpec 进行规范驱动开发的 AI 编码助手使用指南。

## TL;DR 快速检查清单

- 搜索现有工作：`openspec spec list --long`、`openspec list`（仅使用 `rg` 进行全文搜索）

- 确定范围：新增功能还是修改现有功能

- 选择一个唯一的 `change-id`：使用 kebab-case 命名法，动词引导（`add-`、`update-`、`remove-`、`refactor-`）

- 编写脚手架：`proposal.md`、`tasks.md`、`design.md`（仅在需要时编写），以及每个受影响功能的增量规范

- 编写增量：使用 `## ADDED|MODIFIED|REMOVED|RENAMED Requirements`；每个需求至少包含一个 `#### Scenario:`

- 验证：运行 `openspec validate [change-id] --strict` 并修复问题

- 请求批准：提案获得批准前，请勿开始实施

## 三阶段工作流程

### 第一阶段：创建变更

在以下情况下创建提案：

- 添加特性或功能

- 进行重大变更（API、架构）

- 更改架构或模式

- 优化性能（更改行为）

- 更新安全模式

触发条件（示例）：

- “帮我创建变更提案”

- “帮我规划变更”

- “帮我创建提案”

- “我想创建规范提案”

- “我想创建规范”

大致匹配指南：

- 包含以下任一：`proposal`、`change`、`spec`

- 包含以下任一：`create`、`plan`、`make`、`start`、`help`

以下情况无需创建提案：

- 错误修复（恢复预期行为）行为）

- 拼写错误、格式、注释

- 依赖项更新（非破坏性更改）

- 配置更改

- 现有行为的测试

**工作流程**

1. 查看 `openspec/project.md`、`openspec list` 和 `openspec list --specs` 以了解当前上下文。

2. 选择一个唯一的动词引导的 `change-id`，并在 `openspec/changes/<id>/` 目录下创建 `proposal.md`、`tasks.md`、可选的 `design.md` 和规范增量。

3. 使用 `## ADDED|MODIFIED|REMOVED Requirements` 格式草拟规范增量，每个需求至少包含一个 `#### Scenario:`。

4. 运行 `openspec validate <id> --strict` 并解决所有问题，然后再分享提案。

### 第二阶段：实施更改

将这些步骤记录为待办事项，并逐一完成。

1. **阅读 proposal.md** - 了解正在构建的内容

2. **阅读 design.md**（如果存在） - 审查技术决策

3. **阅读 tasks.md** - 获取实施清单

4. **按顺序实施任务** - 按顺序完成

5. **确认完成** - 在更新状态之前，确保 `tasks.md` 中的每个项目都已完成

6. **更新清单** - 所有工作完成后，将每个任务的状态设置为 `- [x]`，以确保清单反映实际情况

7. **审批关卡** - 在提案经过审查和批准之前，不得开始实施

### 第三阶段：归档变更

部署后，创建单独的 PR 来：

- 将 `changes/[name]/` 移动到 `changes/archive/YYYY-MM-DD-[name]/`

- 如果功能发生更改，则更新 `specs/`

- 对于仅涉及工具的更改，请使用 `openspec archive <change-id> --skip-specs --yes` （务必显式传递变更 ID）

- 运行 `openspec validate --strict` 以确认已归档的变更通过检查

## 执行任何任务之前

**上下文检查清单：**

- [ ] 阅读 `specs/[capability]/spec.md` 中的相关规范

- [ ] 检查 `changes/` 中的待处理变更是否存在冲突

- [ ] 阅读 `openspec/project.md` 以了解约定

- [ ] 运行 `openspec list` 以查看活动变更

- [ ] 运行 `openspec list --specs` 以查看现有功能

**创建规范之前：**

- 始终检查功能是否已存在

- 优先修改现有规范，而不是创建重复规范

- 使用 `openspec show [spec]` 查看当前状态

- 如果请求不明确，请在搭建框架之前提出 1-2 个澄清问题

### 搜索指南

- 枚举规范：`openspec spec list --long`（脚本使用 `--json`）

- 枚举变更：`openspec` `openspec list`（或 `openspec change list --json` - 已弃用但仍可用）

- 显示详情：

- 规范：`openspec show <spec-id> --type spec`（使用 `--json` 进行筛选）

- 变更：`openspec show <change-id> --json --deltas-only`

- 全文搜索（使用 ripgrep）：`rg -n "Requirement:|Scenario:" openspec/specs`

## 快速入门

### CLI 命令

```bash

# 基本命令

openspec list # 列出当前变更

openspec list --specs # 列出规范

openspec show [item] # 显示变更或规范

openspec validate [item] # 验证变更或规范

openspec archive <change-id> [--yes|-y] # 部署后归档（添加 --yes 以进行非交互式运行）

# 项目管理

openspec init [path] # 初始化 OpenSpec

openspec update [path] # 更新指令文件

# 交互式模式

openspec show # 选择提示

openspec validate # 批量验证模式

# 调试

openspec show [change] --json --deltas-only

openspec validate [change] --strict

```

### 命令标志

- `--json` - 机器可读输出

- `--type change|spec` - 消除歧义项

- `--strict` - 全面验证

- `--no-interactive` - 禁用提示

- `--skip-specs` - 归档而不更新规范

- `--yes`
- `-y` - 跳过确认提示（非交互式归档）

## 目录结构

```

openspec/

├── project.md # 项目约定

├── specs/ # 当前状态 - 已构建的内容

│ └── [capability]/ # 单个聚焦功能

│ ├── spec.md # 需求和场景

│ └── design.md # 技术模式

├── changes/ # 提案 - 应该更改的内容

│ ├── [change-name]/

│ │ ├── proposal.md # 原因、内容和影响

│ │ ├── tasks.md # 实现清单

│ │ ├── design.md # 技术决策（可选；参见标准）

│ │ └── specs/ # 增量变更

│ │ └── [capability]/

│ │ └── spec.md # 新增/修改/移除

│ └── archive/ # 已完成变更

```

## 创建变更提案

### 决策树

```

新增请求？

├─ 错误修复，恢复规范行为？ → 直接修复

├─ 拼写错误/格式错误/注释错误？ → 直接修复

├─ 新功能/特性？ → 创建提案

├─ 重大变更？ → 创建提案

├─ 架构变更？ → 创建提案

└─ 不清楚？ → 创建提案（更安全）

```

### 提案结构

1. **创建目录：** `changes/[变更 ID]/`（首字母大写，动词开头，唯一）

2. **编写 proposal.md：**

```markdown
# 变更：[变更简述]

## 原因

[1-2 句话描述问题/机遇]

## 变更内容

- [变更列表]

- [用 **BREAKING** 标记重大变更]

## 影响

- 受影响的规范：[列出功能]

- 受影响的代码：[关键文件/系统]

```

3. **创建规范增量：** `specs/[功能]/spec.md`

```markdown

## 新增需求

### 需求：新功能

系统应提供……

#### 场景：成功案例

- **当** 用户执行操作

- **然后** 预期结果

## 修改后的需求

### 需求：现有功能

[完成修改后的需求]

## 移除的需求

### 需求：旧功能

**原因**：[移除原因]

**迁移**：[如何处理]

```
如果多个功能受到影响，请在 `changes/[change-id]/specs/<功能>/spec.md` 下创建多个增量文件，每个功能一个。

4. **创建 tasks.md：**

```markdown

## 1. 实现

- [ ] 1.1 创建数据库模式

- [ ] 1.2 实现 API 端点

- [ ] 1.3 添加前端组件

- [ ] 1.4 编写测试

```

5. **根据需要创建 design.md：**

如果符合以下任何条件，则创建 `design.md`；否则请省略：

- 横切变更（多个服务/模块）或新的架构模式

- 新的外部依赖项或重大数据模型变更

- 安全性、性能或迁移复杂性

- 编码前通过技术决策解决的模糊性

最小化 `design.md` 框架：

```markdown

## 背景

[背景、约束、利益相关者]

## 目标/非目标

- 目标：[...]

- 非目标：[...]

## 决策

- 决策：[是什么以及为什么]

- 考虑的替代方案：[选项 + 理由]

## 风险/权衡

- [风险] → 缓解措施

## 迁移计划

[步骤、回滚]

## 未解决的问题

- [...]

```

## 规范文件格式

### 关键：场景格式

**正确**（使用 #### 标题）：

```markdown

#### 场景：用户登录成功

- **当**提供有效凭证时

- **则**返回 JWT 令牌

```

**错误**（请勿使用项目符号或粗体）：

```markdown

- **场景：用户登录** ❌

**场景：用户登录 ❌

### 场景：用户登录 ❌

```

每个需求必须至少有一个场景。

### 需求措辞

- 对于规范性需求，请使用 SHALL/MUST（除非有意为之，否则避免使用 should/may）。

### 增量操作

- `## 新增需求` - 新增功能

- `## 修改需求` - 更改行为

- `## 移除需求` - 已弃用功能

- `## 重命名需求` - 名称更改

使用 `trim(header)` 匹配标头 - 忽略空格。

#### 何时使用 ADDED 与 MODIFIED

- ADDED：引入一项新的功能或子功能，该功能或子功能可以独立作为一项需求。如果更改是正交的（例如，添加“斜杠命令配置”），而不是改变现有需求的语义，则优先使用 ADDED。

- MODIFIED：更改现有需求的行为、范围或验收标准。始终粘贴完整的、更新后的需求内容（标题 + 所有场景）。归档程序会将整个需求替换为您在此处提供的内容；部分更改会丢弃先前的详细信息。

- RENAMED：仅更改名称时使用。如果您还更改了行为，请使用 RENAMED（名称）+ MODIFIED（内容），并引用新名称。

常见陷阱：使用 MODIFIED 添加新需求时未包含先前的文本。这会导致归档时丢失详细信息。如果您没有明确更改现有需求，请改为在 ADDED 下添加新需求。

正确编写修改后的需求：

1) 在 `openspec/specs/<capability>/spec.md` 中找到现有需求。

2) 复制整个需求块（从 `### 需求：...` 到其场景）。

3) 将其粘贴到在“## 修改要求”下进行编辑，以反映新的行为。

4) 确保标题文本完全匹配（不区分空格），并至少保留一个“#### 场景：”。

重命名示例：

```markdown
## 重命名需求

- 源：`### 需求：登录`

- 目标：`### 需求：用户身份验证`

```

## 故障排除

### 常见错误

**“变更必须至少包含一个增量”**

- 检查 `changes/[name]/specs/` 目录是否存在，且包含 .md 文件

- 验证文件是否包含操作前缀（## 新增需求）

**“需求必须至少包含一个场景”**

- 检查场景是否使用 `#### 场景:` 格式（4 个井号）

- 场景标题不要使用项目符号或粗体

**静默场景解析失败**

- 必须使用完全正确的格式：`#### 场景: 名称`

- 使用以下命令调试：`openspec show [change] --json --deltas-only`

### 验证提示

```bash
# 始终使用严格模式进行全面检查

openspec validate [change] --strict

# 调试增量解析

openspec show [change] --json | jq '.deltas'

# 检查具体需求
openspec show [spec] --json -r 1

```

## 正常流程脚本

```bash

# 1) 探索当前状态

openspec spec list --long

openspec list

# 可选全文搜索：

# rg -n "Requirement:|Scenario:" openspec/specs

# rg -n "^#|Requirement:" openspec/changes

# 2) 选择变更 ID 并创建脚手架

CHANGE=add-two-factor-auth

mkdir -p openspec/changes/$CHANGE/{specs/auth}

printf "## 为什么\n...\n\n## 变更内容\n- ...\n\n## 影响\n- ...\n" > openspec/changes/$CHANGE/proposal.md

printf "## 1. 实现\n- [ ] 1.1 ...\n" > openspec/changes/$CHANGE/tasks.md

# 3) 添加增量（示例）

cat > openspec/changes/$CHANGE/specs/auth/spec.md << 'EOF'

## 新增要求

### 要求：双因素身份验证

用户在登录时必须提供第二个身份验证因素。

#### 场景：需要 OTP

- **当**提供有效凭证时

- **则**需要 OTP 验证

EOF

# 4) 验证

openspec validate $CHANGE --strict

```

## 多功能示例

```

openspec/changes/add-2fa-notify/

├── proposal.md

├── tasks.md

└── specs/

├── auth/

│ └── spec.md # 新增：双因素身份验证

└── notifications/

└── spec.md # 新增：OTP 电子邮件通知

```

auth/spec.md

```markdown

## 新增要求

### 要求：双因素身份验证

...
```

notifications/spec.md

```markdown

## 新增要求

### 要求：OTP电子邮件通知

...
```

## 最佳实践

### 简洁至上

- 默认新代码少于 100 行

- 除非证明不足以满足需求，否则优先使用单文件实现

- 避免使用没有明确理由的框架

- 选择简单但经过验证的模式

### 复杂性触发条件

仅在以下情况下增加复杂性：

- 性能数据显示当前解决方案速度过慢

- 具体的规模需求（>1000 个用户，>100MB 数据）

- 多个经过验证的用例需要抽象

### 清晰的引用

- 使用 `file.ts:42` 格式指定代码位置

- 将规范引用为 `specs/auth/spec.md`

- 链接相关的更改和 PR

### 功能命名

- 使用动词-名词的命名方式：`user-auth`、`payment-capture`

- 每个功能只有一个用途

- 10 分钟内即可理解的原则

- 如果描述需要“AND”，则拆分

### 变更 ID 命名

- 使用 kebab-case 命名法，简短且具有描述性： `add-two-factor-auth`

- 优先使用动词前缀：`add-`、`update-`、`remove-`、`refactor-`

- 确保唯一性；如果已被占用，则添加 `-2`、`-3` 等。

## 工具选择指南

| 任务 | 工具 | 原因 |

|------|------|-----|

| 按模式查找文件 | Glob | 快速模式匹配 |

| 搜索代码内容 | Grep | 优化的正则表达式搜索 |

| 读取特定文件 | Read | 直接文件访问 |

| 探索未知范围 | Task | 多步骤调查 |

## 错误恢复

### 变更冲突

1. 运行 `openspec list` 查看当前变更

2. 检查是否存在重叠的规范

3. 与变更负责人协调

4. 考虑合并提案

### 验证失败

1. 使用 `--strict` 标志运行

2. 检查 JSON 输出以获取详细信息

3. 验证规范文件格式

4. 确保场景格式正确

### 缺少上下文

1. 首先阅读 project.md

2. 检查相关规范

3. 查看最近的归档

4. 请求澄清

## 快速参考

### 阶段指示器

- `changes/` - 已提出，尚未构建

- `specs/` - 已构建并部署

- `archive/` - 已完成的变更

### 文件用途

- `proposal.md` - 原因和内容

- `tasks.md` - 实现步骤

- `design.md` - 技术决策

- `spec.md` - 需求和行为

### CLI 要点

```bash
openspec列表 # 正在进行的项目？

openspec show [item] # 查看详情

openspec validate --strict # 是否正确？

openspec archive <change-id> [--yes|-y] # 标记为完成（添加 --yes 可实现自动化）

```