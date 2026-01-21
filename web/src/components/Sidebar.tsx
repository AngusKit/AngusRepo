import { Home, Package, FileSearch, Upload, ChevronDown, Check, Shield, BarChart3, Eraser, TrendingUp, Activity, Settings, UserCog, Server, UsersRound, Cog, Bell } from 'lucide-react';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from './ui/dropdown-menu';
import { useState } from 'react';
import { useLanguage } from './LanguageProvider';
import { useNavigate, useLocation } from 'react-router-dom';

export function Sidebar() {
  const [selectedApp, setSelectedApp] = useState('AngusRepo');
  const { t } = useLanguage();
  const navigate = useNavigate();
  const location = useLocation();

  const handlePageChange = (page: string) => {
    navigate(`/${page}`);
  };

  const applications = [
    { id: 'angusrepo', name: t('apps.angusrepo'), icon: '📦', description: t('apps.angusrepoDesc') },
    { id: 'angusgit', name: t('apps.angusgit'), icon: '🚀', description: t('apps.angusgitDesc') },
    { id: 'angusai', name: t('apps.angusai'), icon: '🤖', description: t('apps.angusaiDesc') },
    { id: 'chatbot', name: t('apps.chatbot'), icon: '💬', description: t('apps.chatbotDesc') },
    { id: 'analytics', name: t('apps.analytics'), icon: '📊', description: t('apps.analyticsDesc') },
  ];

  const mainMenuItems = [
    { id: 'home', icon: Home, label: t('nav.dashboard') },
    { id: 'repositories', icon: Package, label: t('nav.repositories'), badge: '18' },
    { id: 'artifacts', icon: FileSearch, label: t('nav.artifacts') },
    { id: 'upload', icon: Upload, label: t('nav.upload') },
  ];

  const advancedMenuItems = [
    { id: 'security', icon: Shield, label: t('nav.security'), badge: '3' },
    { id: 'analytics', icon: BarChart3, label: t('nav.analytics') },
    { id: 'cleanup', icon: Eraser, label: t('nav.cleanup') },
  ];

  const activityMenuItems = [
    { id: 'notifications', icon: Bell, label: t('nav.notifications'), badge: '9' },
    { id: 'activity-log', icon: Activity, label: t('nav.activityLog') },
    { id: 'trending', icon: TrendingUp, label: t('nav.trending') },
  ];

  const settingsMenuItems = [
    { id: 'profile-settings', icon: UserCog, label: t('nav.profileSettings') },
    { id: 'repository-settings', icon: Server, label: t('nav.repositorySettings') },
    { id: 'team-management', icon: UsersRound, label: t('nav.teamManagement') },
    { id: 'system-settings', icon: Cog, label: t('nav.systemSettings') },
  ];

  return (
    <aside className="w-64 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 flex flex-col h-screen">
      {/* Application Switcher */}
      <div className="h-16 px-4 border-b border-gray-200 dark:border-gray-700 flex items-center">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="w-full justify-start hover:bg-gray-100 dark:hover:bg-gray-700 h-auto py-2 px-2">
              <div className="flex items-center gap-3 flex-1">
                <div className="size-8 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center flex-shrink-0">
                  <Package className="size-4 text-white" />
                </div>
                <div 
                  className="flex-1 text-left min-w-0 cursor-pointer hover:opacity-80 transition-opacity"
                  onClick={(e) => {
                    e.stopPropagation();
                    handlePageChange('home');
                  }}
                >
                  <div className="text-sm text-gray-900 dark:text-white truncate">{selectedApp}</div>
                  <div className="text-xs text-gray-500 dark:text-gray-400 truncate">{t('apps.workspace')}</div>
                </div>
                <ChevronDown className="size-4 text-gray-400 flex-shrink-0" />
              </div>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent className="w-56">
            {applications.map((app) => (
              <DropdownMenuItem
                key={app.id}
                onClick={() => setSelectedApp(app.name)}
                className="flex items-center gap-2 py-2"
              >
                <span className="text-lg">{app.icon}</span>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="truncate">{app.name}</span>
                    {selectedApp === app.name && <Check className="size-4 text-blue-600" />}
                  </div>
                  <div className="text-xs text-gray-500 dark:text-gray-400 truncate">
                    {app.description}
                  </div>
                </div>
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      {/* Main Navigation */}
      <nav className="flex-1 overflow-y-auto hide-scrollbar p-3">
        <div className="space-y-1">
          {mainMenuItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === `/${item.id}`;
            
            return (
              <Button
                key={item.id}
                variant={isActive ? 'default' : 'ghost'}
                className={`w-full justify-start ${
                  isActive 
                    ? 'bg-blue-600 text-white hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600' 
                    : 'text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700'
                }`}
                onClick={() => handlePageChange(item.id)}
              >
                <Icon className="mr-2 size-4" />
                <span className="flex-1 text-left">{item.label}</span>
                {item.badge && (
                  <Badge 
                    variant={isActive ? 'secondary' : 'default'} 
                    className={`ml-2 ${isActive ? 'bg-blue-700 text-white' : 'bg-gray-200 text-gray-700 dark:bg-gray-700 dark:text-gray-300'}`}
                  >
                    {item.badge}
                  </Badge>
                )}
              </Button>
            );
          })}
        </div>

        {/* Advanced Section */}
        <div className="mt-6">
          <div className="px-2 mb-2 text-xs text-gray-500 dark:text-gray-400">
            {t('nav.advanced')}
          </div>
          <div className="space-y-1">
            {advancedMenuItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === `/${item.id}`;
              
              return (
                <Button
                  key={item.id}
                  variant={isActive ? 'default' : 'ghost'}
                  className={`w-full justify-start ${
                    isActive 
                      ? 'bg-blue-600 text-white hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600' 
                      : 'text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700'
                  }`}
                  onClick={() => handlePageChange(item.id)}
                >
                  <Icon className="mr-2 size-4" />
                  <span className="flex-1 text-left">{item.label}</span>
                  {item.badge && (
                    <Badge 
                      variant={isActive ? 'secondary' : 'default'} 
                      className={`ml-2 ${isActive ? 'bg-blue-700 text-white' : 'bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300'}`}
                    >
                      {item.badge}
                    </Badge>
                  )}
                </Button>
              );
            })}
          </div>
        </div>

        {/* Activity Section */}
        <div className="mt-6">
          <div className="px-2 mb-2 text-xs text-gray-500 dark:text-gray-400">
            {t('nav.activity')}
          </div>
          <div className="space-y-1">
            {activityMenuItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === `/${item.id}`;
              
              return (
                <Button
                  key={item.id}
                  variant={isActive ? 'default' : 'ghost'}
                  className={`w-full justify-start ${
                    isActive 
                      ? 'bg-blue-600 text-white hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600' 
                      : 'text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700'
                  }`}
                  onClick={() => handlePageChange(item.id)}
                >
                  <Icon className="mr-2 size-4" />
                  <span className="flex-1 text-left">{item.label}</span>
                  {item.badge && (
                    <Badge 
                      variant={isActive ? 'secondary' : 'default'} 
                      className={`ml-2 ${isActive ? 'bg-blue-700 text-white' : 'bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300'}`}
                    >
                      {item.badge}
                    </Badge>
                  )}
                </Button>
              );
            })}
          </div>
        </div>

        {/* Settings Section */}
        <div className="mt-6">
          <div className="px-2 mb-2 text-xs text-gray-500 dark:text-gray-400">
            {t('nav.settings')}
          </div>
          <div className="space-y-1">
            {settingsMenuItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === `/${item.id}`;
              
              return (
                <Button
                  key={item.id}
                  variant={isActive ? 'default' : 'ghost'}
                  className={`w-full justify-start ${
                    isActive 
                      ? 'bg-blue-600 text-white hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600' 
                      : 'text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700'
                  }`}
                  onClick={() => handlePageChange(item.id)}
                >
                  <Icon className="mr-2 size-4" />
                  <span className="flex-1 text-left">{item.label}</span>
                </Button>
              );
            })}
          </div>
        </div>
      </nav>
    </aside>
  );
}