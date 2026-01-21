import { Search, Bell, Plus, Sun, Moon, User, Settings, LogOut, Code2, GitPullRequest, MessageSquare, Check, Star, FolderPlus, Users, FileSearch, ClipboardCheck } from 'lucide-react';
import { Button } from './ui/button';
import { Avatar, AvatarFallback, AvatarImage } from './ui/avatar';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger } from './ui/dropdown-menu';
import { Popover, PopoverContent, PopoverTrigger } from './ui/popover';
import { Badge } from './ui/badge';
import { Input } from './ui/input';
import { useState } from 'react';
import { useTheme } from './ThemeProvider';
import { useLanguage } from './LanguageProvider';
import { languages, Language } from '../lib/i18n';
import { useNavigate } from 'react-router-dom';


export function Header() {
  const { language, setLanguage, t } = useLanguage();
  const { theme, setTheme } = useTheme();
  const [notificationOpen, setNotificationOpen] = useState(false);
  const navigate = useNavigate();

  const handleNavigate = (page: string) => {
    navigate(`/${page}`);
  };

  const userInfo = {
    name: 'Alex Chen',
    username: 'alexchen',
    avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBhdmF0YXIlMjBjYXJ0b29ufGVufDF8fHx8MTc2MTEwMTExNXww&ixlib=rb-4.1.0&q=80&w=1080',
  };

  // 获取问候语
  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return t('common.morning');
    if (hour < 18) return t('common.afternoon');
    return t('common.evening');
  };

  // 获取日期字符串
  const getDateString = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth() + 1;
    const date = now.getDate();
    const dayOfWeek = now.getDay();
    
    const weekdays = [
      t('common.sunday'),
      t('common.monday'),
      t('common.tuesday'),
      t('common.wednesday'),
      t('common.thursday'),
      t('common.friday'),
      t('common.saturday'),
    ];

    if (language === 'zh-CN') {
      return `${year}年${month}月${date}日${weekdays[dayOfWeek]}`;
    } else {
      const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'];
      return `${monthNames[month - 1]} ${date}, ${year} ${weekdays[dayOfWeek]}`;
    }
  };

  const notifications = [
    {
      id: 1,
      type: 'pr',
      icon: GitPullRequest,
      title: language === 'zh-CN' ? 'PR 已被合并' : 'PR merged',
      description: language === 'zh-CN' 
        ? 'awesome-ai-project #234: 添加深色主题支持'
        : 'awesome-ai-project #234: Add dark theme support',
      time: language === 'zh-CN' ? '5分钟前' : '5 minutes ago',
      read: false,
    },
    {
      id: 2,
      type: 'mention',
      icon: MessageSquare,
      title: language === 'zh-CN' ? '在 Issue 中提到了你' : 'Mentioned you in an issue',
      description: language === 'zh-CN'
        ? 'react-dashboard #456: 侧边栏性能优化建议'
        : 'react-dashboard #456: Sidebar performance optimization',
      time: language === 'zh-CN' ? '2小时前' : '2 hours ago',
      read: false,
    },
    {
      id: 3,
      type: 'star',
      icon: Star,
      title: language === 'zh-CN' ? '仓库获得了新星标' : 'Repository starred',
      description: language === 'zh-CN'
        ? 'api-gateway 获得了 10 个新星标'
        : 'api-gateway received 10 new stars',
      time: language === 'zh-CN' ? '1天前' : '1 day ago',
      read: true,
    },
  ];

  const unreadCount = notifications.filter(n => !n.read).length;

  return (
    <header className="h-16 bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 px-6 flex items-center justify-between">
      {/* Left Section - Greeting */}
      <div className="flex items-center gap-3">
        <div className="flex items-center gap-2">
          <span className="text-gray-900 dark:text-white">
            {getGreeting()}, <span className="font-bold">{userInfo.name.split(' ')[0]}</span> 👋
          </span>
          <span className="text-gray-500 dark:text-gray-400">
            {t('common.today')} {getDateString()}
          </span>
        </div>
      </div>

      {/* Right Section */}
      <div className="flex items-center gap-2">
        {/* Notifications */}
        <Popover open={notificationOpen} onOpenChange={setNotificationOpen}>
          <PopoverTrigger asChild>
            <Button variant="ghost" size="sm" className="relative">
              <Bell className="size-5" />
              {unreadCount > 0 && (
                <span className="absolute -top-1 -right-1 size-4 bg-blue-600 dark:bg-blue-500 text-white text-xs rounded-full flex items-center justify-center">
                  {unreadCount}
                </span>
              )}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-96 p-0" align="end">
            <div className="p-4 border-b border-gray-200 dark:border-gray-700">
              <div className="flex items-center justify-between">
                <h3 className="text-gray-900 dark:text-white">
                  {language === 'zh-CN' ? '通知' : 'Notifications'}
                </h3>
                <Button variant="ghost" size="sm" onClick={() => setNotificationOpen(false)}>
                  {language === 'zh-CN' ? '全部标为已读' : 'Mark all as read'}
                </Button>
              </div>
            </div>
            <div className="max-h-96 overflow-y-auto">
              {notifications.map((notification) => {
                const Icon = notification.icon;
                return (
                  <div
                    key={notification.id}
                    className={`p-4 border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700/50 cursor-pointer ${
                      !notification.read ? 'bg-blue-50/50 dark:bg-blue-900/10' : ''
                    }`}
                  >
                    <div className="flex gap-3">
                      <div className="flex-shrink-0">
                        <div className="p-2 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
                          <Icon className="size-4 text-blue-600 dark:text-blue-400" />
                        </div>
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm text-gray-900 dark:text-white mb-1">
                          {notification.title}
                        </p>
                        <p className="text-xs text-gray-600 dark:text-gray-400 mb-2 line-clamp-2">
                          {notification.description}
                        </p>
                        <span className="text-xs text-gray-500 dark:text-gray-500">
                          {notification.time}
                        </span>
                      </div>
                      {!notification.read && (
                        <div className="flex-shrink-0">
                          <span className="size-2 bg-blue-600 dark:bg-blue-500 rounded-full block" />
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
            <div className="p-3 border-t border-gray-200 dark:border-gray-700">
              <Button 
                variant="ghost" 
                className="w-full justify-center text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300 hover:bg-blue-50 dark:hover:bg-blue-900/20"
                onClick={() => {
                  setNotificationOpen(false);
                  handleNavigate('notifications');
                }}
              >
                {language === 'zh-CN' ? '查看全部通知' : 'View all notifications'}
              </Button>
            </div>
          </PopoverContent>
        </Popover>

        {/* Language Switcher */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="sm" className="gap-1.5">
              <svg className="size-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M5 8l6 6M4 14l6-6 2-3M2 5h12M7 2h1M22 22l-5-10-5 10M14.5 18h6.5" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              <span>{language === 'zh-CN' ? '中文' : 'English'}</span>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-36">
            {Object.entries(languages).map(([code, lang]) => (
              <DropdownMenuItem
                key={code}
                onClick={() => setLanguage(code as Language)}
                className="flex items-center justify-between"
              >
                <span>{lang.name}</span>
                {language === code && <Check className="size-4 text-blue-600" />}
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>

        {/* Theme Toggle */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="sm">
              {theme === 'dark' ? <Moon className="size-5" /> : <Sun className="size-5" />}
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-36">
            <DropdownMenuItem
              onClick={() => setTheme('light')}
              className="flex items-center justify-between"
            >
              <div className="flex items-center gap-2">
                <Sun className="size-4" />
                <span>{language === 'zh-CN' ? '浅色模式' : 'Light Mode'}</span>
              </div>
              {theme === 'light' && <Check className="size-4 text-blue-600" />}
            </DropdownMenuItem>
            <DropdownMenuItem
              onClick={() => setTheme('dark')}
              className="flex items-center justify-between"
            >
              <div className="flex items-center gap-2">
                <Moon className="size-4" />
                <span>{language === 'zh-CN' ? '深色模式' : 'Dark Mode'}</span>
              </div>
              {theme === 'dark' && <Check className="size-4 text-blue-600" />}
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        {/* User Menu */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="sm" className="gap-2">
              <Avatar className="size-7">
                <AvatarImage src={userInfo.avatar} />
                <AvatarFallback>{userInfo.name.charAt(0)}</AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56">
            <div className="px-2 py-1.5">
              <p className="text-sm text-gray-900 dark:text-white">{userInfo.name}</p>
              <p className="text-xs text-gray-600 dark:text-gray-400">@{userInfo.username}</p>
            </div>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={() => handleNavigate('profile')}>
              <User className="mr-2 size-4" />
              {language === 'zh-CN' ? '个人资料' : 'Your profile'}
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem className="text-red-600 dark:text-red-400">
              <LogOut className="mr-2 size-4" />
              {language === 'zh-CN' ? '退出登录' : 'Sign out'}
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}