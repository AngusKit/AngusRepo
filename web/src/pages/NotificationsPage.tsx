import { Bell, CircleAlert, Star, TrendingUp, Search, CheckCheck, RefreshCw, Check, Eye, Archive, Trash2, AlertTriangle, ShieldAlert, HardDrive, UserPlus, Settings, Package, Trash } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { useState } from 'react';
import { useLanguage } from '@/components/LanguageProvider';

export function NotificationsPage() {
  const { language, t } = useLanguage();
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [selectedType, setSelectedType] = useState('all');
  const [selectedPriority, setSelectedPriority] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  // 通知统计数据
  const stats = [
    {
      id: 'total',
      label: language === 'zh-CN' ? '全部通知' : 'All Notifications',
      value: 34,
      description: language === 'zh-CN' ? '所有消息' : 'All messages',
      icon: Bell,
      bgColor: 'bg-blue-100 dark:bg-blue-900/30',
      iconColor: 'text-blue-600 dark:text-blue-400',
    },
    {
      id: 'pending',
      label: language === 'zh-CN' ? '待处理' : 'Pending',
      value: 9,
      description: language === 'zh-CN' ? '待处理' : 'Pending',
      icon: CircleAlert,
      bgColor: 'bg-orange-100 dark:bg-orange-900/30',
      iconColor: 'text-orange-600 dark:text-orange-400',
    },
    {
      id: 'starred',
      label: language === 'zh-CN' ? '星标消息' : 'Starred',
      value: 5,
      description: language === 'zh-CN' ? '重要消息' : 'Important',
      icon: Star,
      bgColor: 'bg-yellow-100 dark:bg-yellow-900/30',
      iconColor: 'text-yellow-600 dark:text-yellow-400',
    },
    {
      id: 'today',
      label: language === 'zh-CN' ? '今日新增' : 'Today',
      value: 0,
      description: language === 'zh-CN' ? 'vs 昨日' : 'vs Yesterday',
      icon: TrendingUp,
      bgColor: 'bg-green-100 dark:bg-green-900/30',
      iconColor: 'text-green-600 dark:text-green-400',
    },
  ];

  // 消息分类
  const categories = [
    { id: 'all', label: language === 'zh-CN' ? '全部消息' : 'All Messages', count: 34 },
    { id: 'unread', label: language === 'zh-CN' ? '未读消息' : 'Unread', count: 9 },
    { id: 'starred', label: language === 'zh-CN' ? '星标消息' : 'Starred', count: 5 },
    { id: 'archived', label: language === 'zh-CN' ? '已归档' : 'Archived', count: 1 },
  ];

  // 消息类型
  const types = [
    { id: 'all', label: language === 'zh-CN' ? '全部类型' : 'All Types' },
    { id: 'security', label: language === 'zh-CN' ? '安全告警' : 'Security' },
    { id: 'storage', label: language === 'zh-CN' ? '存储空间' : 'Storage' },
    { id: 'access', label: language === 'zh-CN' ? '权限变更' : 'Access' },
    { id: 'artifact', label: language === 'zh-CN' ? '制品更新' : 'Artifact' },
    { id: 'system', label: language === 'zh-CN' ? '系统通知' : 'System' },
  ];

  // 优先级
  const priorities = [
    { id: 'all', label: language === 'zh-CN' ? '全部优先级' : 'All Priority' },
    { id: 'high', label: language === 'zh-CN' ? '高优先级' : 'High' },
    { id: 'medium', label: language === 'zh-CN' ? '中优先级' : 'Medium' },
    { id: 'low', label: language === 'zh-CN' ? '低优先级' : 'Low' },
  ];

  // 通知数据
  const notifications = [
    {
      id: 1,
      type: 'security',
      icon: ShieldAlert,
      title: language === 'zh-CN' ? '发现高危漏洞' : 'Critical Vulnerability Detected',
      description: language === 'zh-CN' 
        ? '仓库 "maven-central-proxy" 中的制品 "log4j-core:2.14.1" 检测到 CVE-2021-44228 高危漏洞'
        : 'Artifact "log4j-core:2.14.1" in repository "maven-central-proxy" has critical CVE-2021-44228',
      tags: [
        { label: language === 'zh-CN' ? '安全扫描' : 'Security', color: 'red' },
        { label: language === 'zh-CN' ? '高优先级' : 'High', color: 'orange' },
      ],
      status: language === 'zh-CN' ? '信息' : 'Info',
      read: false,
      starred: false,
      time: '2026/01/19 14:58:36',
    },
    {
      id: 2,
      type: 'storage',
      icon: HardDrive,
      title: language === 'zh-CN' ? '存储空间告警' : 'Storage Quota Warning',
      description: language === 'zh-CN' 
        ? '仓库 "docker-hosted" 存储使用率已达 85%，建议及时清理或扩容'
        : 'Repository "docker-hosted" storage usage reached 85%, cleanup or expansion recommended',
      tags: [
        { label: language === 'zh-CN' ? '存储管理' : 'Storage', color: 'orange' },
        { label: language === 'zh-CN' ? '中优先级' : 'Medium', color: 'blue' },
      ],
      status: language === 'zh-CN' ? '警告' : 'Warning',
      read: false,
      starred: true,
      time: '2026/01/19 14:44:45',
    },
    {
      id: 3,
      type: 'artifact',
      icon: Package,
      title: language === 'zh-CN' ? '新制品上传成功' : 'Artifact Uploaded',
      description: language === 'zh-CN' 
        ? '用户 "alex.chen" 向仓库 "npm-hosted" 上传了制品 "@angular/core:17.2.0"'
        : 'User "alex.chen" uploaded artifact "@angular/core:17.2.0" to repository "npm-hosted"',
      tags: [
        { label: language === 'zh-CN' ? '制品管理' : 'Artifact', color: 'green' },
        { label: language === 'zh-CN' ? '低优先级' : 'Low', color: 'gray' },
      ],
      status: language === 'zh-CN' ? '成功' : 'Success',
      read: true,
      starred: false,
      time: '2026/01/19 12:18:32',
    },
    {
      id: 4,
      type: 'access',
      icon: UserPlus,
      title: language === 'zh-CN' ? '权限变更通知' : 'Access Permission Changed',
      description: language === 'zh-CN' 
        ? '管理员 "admin" 授予用户 "zhang.san" 对仓库 "maven-releases" 的上传权限'
        : 'Admin "admin" granted upload permission to user "zhang.san" for repository "maven-releases"',
      tags: [
        { label: language === 'zh-CN' ? '权限管理' : 'Access', color: 'blue' },
        { label: language === 'zh-CN' ? '中优先级' : 'Medium', color: 'blue' },
      ],
      status: language === 'zh-CN' ? '信息' : 'Info',
      read: false,
      starred: true,
      time: '2026/01/19 11:25:18',
    },
    {
      id: 5,
      type: 'security',
      icon: ShieldAlert,
      title: language === 'zh-CN' ? '安全扫描完成' : 'Security Scan Completed',
      description: language === 'zh-CN' 
        ? '仓库 "npm-proxy" 安全扫描完成，发现 3 个中危漏洞，0 个高危漏洞'
        : 'Security scan completed for "npm-proxy", found 3 medium and 0 critical vulnerabilities',
      tags: [
        { label: language === 'zh-CN' ? '安全扫描' : 'Security', color: 'yellow' },
        { label: language === 'zh-CN' ? '中优先级' : 'Medium', color: 'blue' },
      ],
      status: language === 'zh-CN' ? '信息' : 'Info',
      read: true,
      starred: false,
      time: '2026/01/19 10:15:42',
    },
    {
      id: 6,
      type: 'system',
      icon: Settings,
      title: language === 'zh-CN' ? '系统维护通知' : 'System Maintenance',
      description: language === 'zh-CN' 
        ? '系统将于 2026/01/20 02:00-04:00 进行例行维护，期间服务可能短暂中断'
        : 'System maintenance scheduled on 2026/01/20 02:00-04:00, services may be temporarily unavailable',
      tags: [
        { label: language === 'zh-CN' ? '系统通知' : 'System', color: 'purple' },
        { label: language === 'zh-CN' ? '高优先级' : 'High', color: 'orange' },
      ],
      status: language === 'zh-CN' ? '信息' : 'Info',
      read: false,
      starred: true,
      time: '2026/01/19 09:30:00',
    },
    {
      id: 7,
      type: 'artifact',
      icon: Trash,
      title: language === 'zh-CN' ? '清理任务完成' : 'Cleanup Task Completed',
      description: language === 'zh-CN' 
        ? '仓库 "docker-hosted" 清理任务已完成，删除了 15 个过期镜像，释放空间 2.3 GB'
        : 'Cleanup task completed for "docker-hosted", removed 15 expired images, freed 2.3 GB',
      tags: [
        { label: language === 'zh-CN' ? '清理任务' : 'Cleanup', color: 'green' },
        { label: language === 'zh-CN' ? '低优先级' : 'Low', color: 'gray' },
      ],
      status: language === 'zh-CN' ? '成功' : 'Success',
      read: true,
      starred: false,
      time: '2026/01/19 08:00:00',
    },
    {
      id: 8,
      type: 'storage',
      icon: AlertTriangle,
      title: language === 'zh-CN' ? '配额即将耗尽' : 'Quota Almost Exhausted',
      description: language === 'zh-CN' 
        ? '仓库 "pypi-hosted" 已使用 95% 配额，建议立即处理以避免服务中断'
        : 'Repository "pypi-hosted" used 95% quota, immediate action recommended to avoid service disruption',
      tags: [
        { label: language === 'zh-CN' ? '存储管理' : 'Storage', color: 'red' },
        { label: language === 'zh-CN' ? '高优先级' : 'High', color: 'orange' },
      ],
      status: language === 'zh-CN' ? '紧急' : 'Urgent',
      read: false,
      starred: true,
      time: '2026/01/18 23:45:12',
    },
    {
      id: 9,
      type: 'access',
      icon: UserPlus,
      title: language === 'zh-CN' ? '新用户注册' : 'New User Registration',
      description: language === 'zh-CN' 
        ? '新用户 "li.si" 已成功注册，等待管理员审核激活'
        : 'New user "li.si" registered successfully, pending admin approval',
      tags: [
        { label: language === 'zh-CN' ? '用户管理' : 'User', color: 'blue' },
        { label: language === 'zh-CN' ? '低优先级' : 'Low', color: 'gray' },
      ],
      status: language === 'zh-CN' ? '信息' : 'Info',
      read: true,
      starred: false,
      time: '2026/01/18 18:20:30',
    },
  ];

  const getTagColor = (color: string) => {
    const colors: Record<string, string> = {
      red: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
      orange: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
      yellow: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      green: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      blue: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      purple: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
      gray: 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400',
    };
    return colors[color] || colors.gray;
  };

  // 筛选通知
  const filteredNotifications = notifications.filter((notification) => {
    // 分类筛选
    if (selectedCategory === 'unread' && notification.read) return false;
    if (selectedCategory === 'starred' && !notification.starred) return false;
    if (selectedCategory === 'archived') return false; // 暂无归档数据
    
    // 类型筛选
    if (selectedType !== 'all' && notification.type !== selectedType) return false;
    
    // 优先级筛选（从tags中获取）
    if (selectedPriority !== 'all') {
      const priorityMap: Record<string, string> = {
        'high': language === 'zh-CN' ? '高优先级' : 'High',
        'medium': language === 'zh-CN' ? '中优先级' : 'Medium',
        'low': language === 'zh-CN' ? '低优先级' : 'Low',
      };
      const hasPriority = notification.tags.some(tag => tag.label === priorityMap[selectedPriority]);
      if (!hasPriority) return false;
    }
    
    // 搜索筛选
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      return notification.title.toLowerCase().includes(query) ||
             notification.description.toLowerCase().includes(query);
    }
    
    return true;
  });

  // 分页计算
  const totalPages = Math.ceil(filteredNotifications.length / itemsPerPage);
  const currentNotifications = filteredNotifications.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  // 当筛选条件变化时重置页码
  const handleFilterChange = (setter: (value: string) => void, value: string) => {
    setter(value);
    setCurrentPage(1);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 dark:text-white mb-2">
          {language === 'zh-CN' ? '消息通知' : 'Notifications'}
        </h1>
        <p className="text-gray-600 dark:text-gray-400">
          {language === 'zh-CN' ? '查看和管理系统通知消息' : 'View and manage system notifications'}
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div
              key={stat.id}
              className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-4"
            >
              <div className="flex items-start justify-between">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{stat.label}</p>
                  <p className="text-2xl text-gray-900 dark:text-white mb-1">{stat.value}</p>
                  <p className="text-xs text-gray-500 dark:text-gray-500">{stat.description}</p>
                </div>
                <div className={`p-2 rounded-lg ${stat.bgColor}`}>
                  <Icon className={`size-5 ${stat.iconColor}`} />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Left Sidebar - Filters */}
        <div className="lg:col-span-1 space-y-6">
          {/* Category Filter */}
          <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg">
            <div className="p-4 border-b border-gray-200 dark:border-gray-700">
              <h3 className="text-sm text-gray-900 dark:text-white">
                {language === 'zh-CN' ? '消息分类' : 'Category'}
              </h3>
            </div>
            <div className="p-2">
              {categories.map((category) => (
                <button
                  key={category.id}
                  onClick={() => handleFilterChange(setSelectedCategory, category.id)}
                  className={`w-full flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-colors ${
                    selectedCategory === category.id
                      ? 'bg-gray-900 dark:bg-gray-700 text-white'
                      : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50'
                  }`}
                >
                  <span>{category.label}</span>
                  <span
                    className={`text-xs px-2 py-0.5 rounded ${
                      selectedCategory === category.id
                        ? 'bg-white/20 text-white'
                        : 'bg-gray-200 dark:bg-gray-700 text-gray-600 dark:text-gray-400'
                    }`}
                  >
                    {category.count}
                  </span>
                </button>
              ))}
            </div>
          </div>

          {/* Type Filter */}
          <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg">
            <div className="p-4 border-b border-gray-200 dark:border-gray-700">
              <h3 className="text-sm text-gray-900 dark:text-white">
                {language === 'zh-CN' ? '消息类型' : 'Type'}
              </h3>
            </div>
            <div className="p-2">
              <select
                value={selectedType}
                onChange={(e) => handleFilterChange(setSelectedType, e.target.value)}
                className="w-full px-3 py-2 bg-gray-50 dark:bg-gray-900 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-white"
              >
                {types.map((type) => (
                  <option key={type.id} value={type.id}>
                    {type.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Priority Filter */}
          <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg">
            <div className="p-4 border-b border-gray-200 dark:border-gray-700">
              <h3 className="text-sm text-gray-900 dark:text-white">
                {language === 'zh-CN' ? '优先级' : 'Priority'}
              </h3>
            </div>
            <div className="p-2">
              <select
                value={selectedPriority}
                onChange={(e) => handleFilterChange(setSelectedPriority, e.target.value)}
                className="w-full px-3 py-2 bg-gray-50 dark:bg-gray-900 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-white"
              >
                {priorities.map((priority) => (
                  <option key={priority.id} value={priority.id}>
                    {priority.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* Right Content - Notifications List */}
        <div className="lg:col-span-3">
          <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg">
            {/* Search and Actions */}
            <div className="p-4 border-b border-gray-200 dark:border-gray-700">
              <div className="flex items-center gap-3">
                <div className="relative flex-1">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 size-4 text-gray-400 dark:text-gray-500" />
                  <Input
                    placeholder={language === 'zh-CN' ? '搜索消息...' : 'Search notifications...'}
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10 bg-gray-50 dark:bg-gray-900"
                  />
                </div>
                <Button variant="outline" size="sm" className="gap-2">
                  <CheckCheck className="size-4" />
                  {language === 'zh-CN' ? '全部已读' : 'Mark All Read'}
                </Button>
                <Button variant="outline" size="sm">
                  <RefreshCw className="size-4" />
                </Button>
              </div>
            </div>

            {/* Notifications List */}
            <div className="divide-y divide-gray-200 dark:divide-gray-700">
              {currentNotifications.map((notification) => {
                const Icon = notification.icon;
                return (
                  <div
                    key={notification.id}
                    className={`p-4 hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors ${
                      !notification.read ? 'bg-blue-50/30 dark:bg-blue-900/5' : ''
                    }`}
                  >
                    <div className="flex gap-3">
                      {/* Icon */}
                      <div className="flex-shrink-0">
                        <div className="p-2 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
                          <Icon className="size-5 text-blue-600 dark:text-blue-400" />
                        </div>
                      </div>

                      {/* Content */}
                      <div className="flex-1 min-w-0">
                        {/* Title and Status */}
                        <div className="flex items-start justify-between gap-3 mb-2">
                          <h4 className="text-sm text-gray-900 dark:text-white">
                            {notification.title}
                          </h4>
                          <Badge
                            variant="secondary"
                            className="flex-shrink-0 bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400"
                          >
                            {notification.status}
                          </Badge>
                        </div>

                        {/* Description */}
                        <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                          {notification.description}
                        </p>

                        {/* Tags and Actions */}
                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-2">
                            {notification.tags.map((tag, index) => (
                              <span
                                key={index}
                                className={`text-xs px-2 py-1 rounded ${getTagColor(tag.color)}`}
                              >
                                {tag.label}
                              </span>
                            ))}
                          </div>

                          <div className="flex items-center gap-2">
                            <Button
                              variant="ghost"
                              size="sm"
                              className="h-8 px-2 text-gray-600 dark:text-gray-400"
                              title={language === 'zh-CN' ? '标记已读' : 'Mark as read'}
                            >
                              <Check className="size-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              className="h-8 px-2 text-gray-600 dark:text-gray-400"
                              title={language === 'zh-CN' ? '查看' : 'View'}
                            >
                              <Eye className="size-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              className="h-8 px-2 text-gray-600 dark:text-gray-400"
                              title={language === 'zh-CN' ? '归档' : 'Archive'}
                            >
                              <Archive className="size-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              className="h-8 px-2 text-red-600 dark:text-red-400"
                              title={language === 'zh-CN' ? '删除' : 'Delete'}
                            >
                              <Trash2 className="size-4" />
                            </Button>
                          </div>
                        </div>

                        {/* Time */}
                        <div className="flex items-center justify-between mt-3 pt-3 border-t border-gray-200 dark:border-gray-700">
                          <span className="text-xs text-gray-500 dark:text-gray-500">
                            {notification.time}
                          </span>
                          {notification.starred && (
                            <Star className="size-4 text-yellow-500 fill-yellow-500" />
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Pagination */}
            <div className="p-4 flex items-center justify-between">
              <Button
                variant="outline"
                size="sm"
                disabled={currentPage === 1}
                onClick={() => setCurrentPage(currentPage - 1)}
              >
                {language === 'zh-CN' ? '上一页' : 'Previous'}
              </Button>
              <span className="text-sm text-gray-500 dark:text-gray-400">
                {language === 'zh-CN'
                  ? `第 ${currentPage} 页，共 ${totalPages} 页`
                  : `Page ${currentPage} of ${totalPages}`}
              </span>
              <Button
                variant="outline"
                size="sm"
                disabled={currentPage * itemsPerPage >= filteredNotifications.length}
                onClick={() => setCurrentPage(currentPage + 1)}
              >
                {language === 'zh-CN' ? '下一页' : 'Next'}
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}